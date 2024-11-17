package site.balpyo.script.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

import org.checkerframework.checker.units.qual.t;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.data.jpa.domain.Specification;
import site.balpyo.ai.controller.PollyController;
import site.balpyo.ai.dto.PollyDTO;
import site.balpyo.ai.dto.upload.UploadResultDTO;
import site.balpyo.ai.service.PollyService;
import site.balpyo.auth.service.AuthenticationService;
import site.balpyo.script.dto.ScriptDto;
import site.balpyo.script.entity.ETag;
import site.balpyo.script.entity.Script;
import site.balpyo.script.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.balpyo.script.repository.ScriptSpecifications;

import java.util.Optional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static site.balpyo.script.service.SpeechMarkUtil.convertSpeechMarksToString;

@Log4j2
@Service
public class ScriptService {

    @Autowired
    private ScriptRepository repository;

    @Autowired
    private PollyService pollyService;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    // Get all scripts and convert them to ScriptDto
    public List<ScriptDto> getAllScripts() {

        return repository.findAllByUser(authenticationService.authenticationToUser()).stream()
                .map(Script::toDto) // Convert each Script entity to ScriptDto
                .toList(); // Convert the stream back to a list
    }

    @Transactional
    public ScriptDto getScriptById(Long id) {
        return repository.findByIdAndUser(id, authenticationService.authenticationToUser())
                .map(Script::toDto)
                .orElse(null);
    }

    @Transactional
    public ScriptDto createScript(ScriptDto scriptDto) {
        scriptDto.setIsGenerating(false);
        scriptDto.setUseAi(false);

        Script script = scriptDto.toEntity();
        script.setTags(ETag.SCRIPT.name());
        script.setUser(authenticationService.authenticationToUser());
        Script savedScript = repository.save(script);

        return savedScript.toDto();
    }

    @Transactional
    public ScriptDto updateUncalScript(Long id, ScriptDto scriptDto) {

        // ID와 User로 Script 찾기
        Optional<Script> opExistingScript = repository.findByIdAndUser(id, authenticationService.authenticationToUser());

        if (opExistingScript.isPresent()) {
            // Script가 존재하면 업데이트
            Script script = opExistingScript.get();
            script.setTitle(scriptDto.getTitle());
            if(scriptDto.getOriginalScript()!=null){
                script.setContent(scriptDto.getOriginalScript());
            }
            if(scriptDto.getIsGenerating()!=null){
                script.setIsGenerating(scriptDto.getIsGenerating());
            }

            script.setOriginalScript(scriptDto.getOriginalScript());
            Script savedScript = repository.save(script);
            return savedScript.toDto();
        } else {
            // Script가 없으면 예외 발생
            throw new IllegalArgumentException("Script not found with ID: " + id);
        }
    }

    @Transactional
    // Delete a script by ID
    public void deleteScript(Long id) {
        repository.deleteByIdAndUser(id, authenticationService.authenticationToUser());
    }

    public List<ScriptDto> getAllScriptByTagAndIsGenerating(String tag, Boolean isGenerating, String searchValue) {
        Specification<Script> spec = Specification.where(ScriptSpecifications.hasTag(tag))
                .and(ScriptSpecifications.isGenerating(isGenerating))
                .and(ScriptSpecifications.containsSearchValue(searchValue));

        List<Script> scripts = repository.findAll(spec);

        // ScriptDto로 변환하여 반환
        return scripts.stream().map(Script::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ScriptDto createScriptAndGetTime(ScriptDto scriptDto)
            throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        PollyDTO pollyDTO = new PollyDTO();
        pollyDTO.setText(scriptDto.getContent());
        pollyDTO.setSpeed(scriptDto.getSpeed());

        UploadResultDTO uploadResultDTO = pollyService.synthesizeAndUploadSpeech(pollyDTO);

        scriptDto.setVoiceFilePath(uploadResultDTO.getVoiceFilePath());
        scriptDto.setPlayTime(uploadResultDTO.getPlayTime());
        scriptDto.setTags(List.of(ETag.TIME.name()));
        scriptDto.setSpeechMark(uploadResultDTO.getSpeechMarks());

        return createScript(scriptDto);
    }

    @Transactional
    public ScriptDto createNoteScript(ScriptDto scriptDto) {
        scriptDto.setIsGenerating(false);
        scriptDto.setUseAi(false);

        Script script = scriptDto.toEntity();
        script.setTags(ETag.NOTE.name());
        script.setUser(authenticationService.authenticationToUser());
        Script savedScript = repository.save(script);

        return savedScript.toDto();
    }

    @Transactional
    public ScriptDto updateScript(Long id, ScriptDto scriptDto) {

        log.info("Updating script with ID: {}", id);
        log.info(scriptDto);

        Script script = repository.findByIdAndUser(id, authenticationService.authenticationToUser())
                .orElseThrow(() -> new IllegalArgumentException("Script not found"));


        PollyDTO pollyDTO = new PollyDTO();
        pollyDTO.setText(scriptDto.getContent());
        pollyDTO.setSpeed(scriptDto.getSpeed());

        UploadResultDTO uploadResultDTO = pollyService.synthesizeAndUploadSpeech(pollyDTO);

        scriptDto.setVoiceFilePath(uploadResultDTO.getVoiceFilePath());
        scriptDto.setPlayTime(uploadResultDTO.getPlayTime());
        scriptDto.setTags(List.of(ETag.TIME.name()));
        scriptDto.setSpeechMark(uploadResultDTO.getSpeechMarks());
        scriptDto.setSpeed(scriptDto.getSpeed());


        if (script != null) {
            log.info("---------------- script found ----------------");
            Script updatedScript = scriptDto.toEntity();
            updatedScript.setId(id);
            updatedScript.setTags(ETag.SCRIPT.name());
            updatedScript.setUser(authenticationService.authenticationToUser());
            Script savedScript = repository.save(updatedScript);
            return savedScript.toDto();
        } else {
            log.info("---------------- script not found ----------------");
            Script newScript = scriptDto.toEntity();
            newScript.setTags(ETag.SCRIPT.name());
            newScript.setUser(authenticationService.authenticationToUser());
            Script savedScript = repository.save(newScript);
            return savedScript.toDto();
        }

    }

}

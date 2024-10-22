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
        return repository.findByIdAndUser(id, authenticationService.authenticationToUser())
                .map(existingScript -> {
                    boolean isUpdated = false; // Flag to track if any changes were made

                    // Create updated script from DTO
                    Script updatedScript = scriptDto.toEntity();
                    updatedScript.setId(existingScript.getId());

                    // Compare fields and update only if necessary
                    if (!existingScript.getTitle().equals(scriptDto.getTitle())) {
                        updatedScript.setTitle(scriptDto.getTitle());
                        isUpdated = true;
                    } else {
                        updatedScript.setTitle(existingScript.getTitle());
                    }

                    if (!existingScript.getContent().equals(scriptDto.getContent())) {
                        updatedScript.setContent(scriptDto.getContent());
                        isUpdated = true;
                    } else {
                        updatedScript.setContent(existingScript.getContent());
                    }

                    System.out.println("원래존재하던 태그" + existingScript.getTags().toString());
                    updatedScript.setTags(existingScript.getTags());

                    // Add more comparisons for other fields as needed
                    // If any field was updated, save and return the updated entity
                    if (isUpdated) {
                        Script savedScript = repository.save(updatedScript);
                        return savedScript.toDto();
                    }

                    // If no changes, return the existing script without updating
                    return existingScript.toDto();
                }).orElse(null);
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

        scriptDto.setUseAi(true);

        log.info("Updating script with ID: {}", id);
        log.info(scriptDto);

        Script script = repository.findByIdAndUser(id, authenticationService.authenticationToUser())
                .orElseThrow(() -> new IllegalArgumentException("Script not found"));

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

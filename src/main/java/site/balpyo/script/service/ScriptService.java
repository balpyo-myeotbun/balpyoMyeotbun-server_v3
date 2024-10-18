package site.balpyo.script.service;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.data.jpa.domain.Specification;
import site.balpyo.ai.controller.PollyController;
import site.balpyo.ai.dto.PollyDTO;
import site.balpyo.ai.dto.upload.UploadResultDTO;
import site.balpyo.ai.service.PollyService;
import site.balpyo.script.dto.ScriptDto;
import site.balpyo.script.entity.ETag;
import site.balpyo.script.entity.Script;
import site.balpyo.script.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.balpyo.script.repository.ScriptSpecifications;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static site.balpyo.script.service.SpeechMarkUtil.convertSpeechMarksToString;

@Service
public class ScriptService {

    @Autowired
    private ScriptRepository repository;

    @Autowired
    private PollyService pollyService;

    // Get all scripts and convert them to ScriptDto
    public List<ScriptDto> getAllScripts() {
        return repository.findAll().stream()
                .map(Script::toDto)  // Convert each Script entity to ScriptDto
                .toList();  // Convert the stream back to a list
    }


    public ScriptDto getScriptById(Long id) {
        return repository.findById(id)
                .map(Script::toDto)
                .orElse(null);
    }



    public ScriptDto createScript(ScriptDto scriptDto) {
        scriptDto.setIsGenerating(false);
        scriptDto.setUseAi(false);

        Script script = scriptDto.toEntity();
        script.setTags(ETag.SCRIPT.name());
        Script savedScript = repository.save(script);

        return savedScript.toDto();
    }


    public ScriptDto updateUncalScript(Long id, ScriptDto scriptDto) {
        return repository.findById(id)
                .map(existingScript -> {
                    boolean isUpdated = false; // Flag to track if any changes were made

                    // Create updated script from DTO
                    Script updatedScript = scriptDto.toEntity();
                    updatedScript.setScriptId(existingScript.getScriptId());

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

                    System.out.println("원래존재하던 태그"+existingScript.getTags().toString());
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


    // Delete a script by ID
    public void deleteScript(Long id) {
        repository.deleteById(id);
    }

    public List<ScriptDto> getAllScriptByTagAndIsGenerating(String tag, Boolean isGenerating, String searchValue) {
        Specification<Script> spec = Specification.where(ScriptSpecifications.hasTag(tag))
                .and(ScriptSpecifications.isGenerating(isGenerating))
                .and(ScriptSpecifications.containsSearchValue(searchValue));

        List<Script> scripts = repository.findAll(spec);

        // ScriptDto로 변환하여 반환
        return scripts.stream().map(Script::toDto).collect(Collectors.toList());
    }
    public ScriptDto createScriptAndGetTime(ScriptDto scriptDto) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        PollyDTO pollyDTO = new PollyDTO();
        pollyDTO.setText(scriptDto.getContent());
        pollyDTO.setSpeed(scriptDto.getSpeed());

        UploadResultDTO uploadResultDTO = pollyService.synthesizeAndUploadSpeech(pollyDTO);

        scriptDto.setFilePath(uploadResultDTO.getProfileUrl());
        scriptDto.setPlayTime(uploadResultDTO.getPlayTime());
        scriptDto.setTags(List.of(ETag.TIME.name()));
        scriptDto.setSpeechMark(uploadResultDTO.getSpeechMarks());

        return createScript(scriptDto);
    }

    public ScriptDto createNoteScript(ScriptDto scriptDto) {
        scriptDto.setIsGenerating(false);
        scriptDto.setUseAi(false);

        Script script = scriptDto.toEntity();
        script.setTags(ETag.NOTE.name());
        Script savedScript = repository.save(script);

        return savedScript.toDto();
    }

    public ScriptDto updateScript(Long id, ScriptDto scriptDto) {

        return repository.findById(id)
                .map(existingScript -> {
                    boolean isUpdated = false; // Flag to track if any changes were made

                    // Create updated script from DTO
                    Script updatedScript = scriptDto.toEntity();
                    updatedScript.setScriptId(existingScript.getScriptId());

                    // Check if the content or speed has changed, update if necessary
                    if (!existingScript.getContent().equals(scriptDto.getContent()) ||
                            !existingScript.getSpeed().equals(scriptDto.getSpeed())) {

                        PollyDTO pollyDTO = new PollyDTO();
                        pollyDTO.setText(scriptDto.getContent());
                        pollyDTO.setSpeed(scriptDto.getSpeed());

                        UploadResultDTO uploadResultDTO = pollyService.synthesizeAndUploadSpeech(pollyDTO);

                        updatedScript.setFilePath(uploadResultDTO.getProfileUrl());
                        updatedScript.setPlayTime(uploadResultDTO.getPlayTime());
                        updatedScript.setSpeechMark(convertSpeechMarksToString(uploadResultDTO.getSpeechMarks()));

                        isUpdated = true; // Mark as updated since content or speed has changed
                    } else {
                        // Keep the existing values if no changes were detected
                        updatedScript.setFilePath(existingScript.getFilePath());
                        updatedScript.setPlayTime(existingScript.getPlayTime());
                        updatedScript.setSpeechMark(existingScript.getSpeechMark());
                    }

                    System.out.println("원래존재하던 태그"+existingScript.getTags().toString());
                    updatedScript.setTags(existingScript.getTags());

                    // Save only if any changes were made
                    if (isUpdated) {
                        Script savedScript = repository.save(updatedScript);
                        return savedScript.toDto();
                    }

                    return existingScript.toDto(); // Return the existing script if no changes
                }).orElse(null);
    }

}


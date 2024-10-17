package site.balpyo.script.service;

import site.balpyo.script.dto.ScriptDto;
import site.balpyo.script.entity.ETag;
import site.balpyo.script.entity.Script;
import site.balpyo.script.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    @Autowired
    private ScriptRepository repository;
    private SpeechMarkUtil speechMarkUtil;

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


    public ScriptDto updateScript(Long id, ScriptDto scriptDto) {
        return repository.findById(id)
                .map(existingScript -> {
                    Script updatedScript = scriptDto.toEntity();
                    updatedScript.setScriptId(existingScript.getScriptId());
                    Script savedScript = repository.save(updatedScript);
                    return savedScript.toDto();
                }).orElse(null);
    }

    // Delete a script by ID
    public void deleteScript(Long id) {
        repository.deleteById(id);
    }

    public List<ScriptDto> getAllScriptByTagAndIsGenerating(String tag, Boolean isGenerating) {
        List<Script> scripts;

        if (tag != null && isGenerating != null) {
            // 태그와 isGenerating 둘 다 있는 경우
            scripts = repository.findByTagsContainingAndIsGenerating(tag, isGenerating);
        } else if (tag != null) {
            // 태그만 있는 경우
            scripts = repository.findByTagsContaining(tag);
        } else if (isGenerating != null) {
            // isGenerating만 있는 경우
            scripts = repository.findByIsGenerating(isGenerating);
        } else {
            // 둘 다 없으면 모든 스크립트를 반환 (원하는 경우에만 사용)
            scripts = repository.findAll();
        }

        // ScriptDto로 변환하여 반환
        return scripts.stream().map(Script::toDto).collect(Collectors.toList());
    }
}


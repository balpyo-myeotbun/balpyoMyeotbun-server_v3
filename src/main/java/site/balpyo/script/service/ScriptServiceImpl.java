package site.balpyo.script.service;

import site.balpyo.script.dto.ScriptDto;
import site.balpyo.script.entity.ETag;
import site.balpyo.script.entity.Script;
import site.balpyo.script.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScriptServiceImpl {

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
}


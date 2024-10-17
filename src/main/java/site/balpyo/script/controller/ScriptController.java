package site.balpyo.script.controller;

import site.balpyo.script.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.balpyo.script.service.ScriptServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    @Autowired
    private ScriptServiceImpl service;

    @GetMapping
    public List<ScriptDto> getAllScripts() {
        return service.getAllScripts();
    }

    @GetMapping("/{id}")
    public ScriptDto getScriptById(@PathVariable Long id) {
        return service.getScriptById(id);
    }

    @PostMapping
    public ScriptDto createScript(@RequestBody ScriptDto scriptDto) {
        return service.createScript(scriptDto);
    }

    @PutMapping("/{id}")
    public ScriptDto updateScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateScript(id, scriptDto);
    }

    @DeleteMapping("/{id}")
    public void deleteScript(@PathVariable Long id) {
        service.deleteScript(id);
    }
}

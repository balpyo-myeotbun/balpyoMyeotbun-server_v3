package site.balpyo.script.controller;

import io.swagger.v3.oas.annotations.Operation;
import site.balpyo.ai.service.GenerateScriptService;
import site.balpyo.script.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.balpyo.script.service.ScriptService;

import java.util.List;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    @Autowired
    private ScriptService service;

    @Autowired
    private GenerateScriptService aiService;


    @Operation(summary = "Get all scripts", description = "Fetch all saved scripts from the database")
    @GetMapping
    public List<ScriptDto> getAllScripts() {
        return service.getAllScripts();
    }

    @GetMapping("/{id}")
    public ScriptDto getScriptById(@PathVariable Long id) {
        return service.getScriptById(id);
    }


    @GetMapping("/search")
    public List<ScriptDto> getScriptByTagAndIsGenerating(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean isGenerating) {
        // tag와 isGenerating 값을 기준으로 검색
        return service.getAllScriptByTagAndIsGenerating(tag, isGenerating);
    }


    @Operation(summary = "자체 스크립트 생성")
    @PostMapping
    public ScriptDto createScript(@RequestBody ScriptDto scriptDto) {
        return service.createScript(scriptDto);
    }


    @Operation(summary = "AI스크립트 생성", description = "필수값 : ScriptDto ->"
            +"    \"title\",\n" +
            "    \"topic\",\n" +
            "    \"keywords\",\n" +
            "    \"secTime\",\n" )
    @PostMapping("/generate")
    public ScriptDto generateAndSaveScript(@RequestBody ScriptDto scriptDto) {
        return aiService.generateAiScriptAndSave(scriptDto);
    }

    @Operation(summary = "스크립트 데이터 수정", description = "원하는 필드값을 삽입해주세요")
    @PutMapping("/{id}")
    public ScriptDto updateScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateScript(id, scriptDto);
    }

    @DeleteMapping("/{id}")
    public void deleteScript(@PathVariable Long id) {
        service.deleteScript(id);
    }

}

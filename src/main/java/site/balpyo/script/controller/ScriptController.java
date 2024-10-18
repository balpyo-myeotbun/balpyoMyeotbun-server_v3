package site.balpyo.script.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import site.balpyo.ai.service.GenerateScriptService;
import site.balpyo.script.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.balpyo.script.service.ScriptService;

import java.io.IOException;
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

    @Operation(summary = "tag혹은 isGenerating(ai가 스크립트를 생성중인지)를 기준으로 스크립트를 반환하는 API")
    @GetMapping("/search")
    public List<ScriptDto> getScriptByTagAndIsGenerating(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean isGenerating,
    @RequestParam(required = false) String searchValue) {
        // tag와 isGenerating 값을 기준으로 검색
        return service.getAllScriptByTagAndIsGenerating(tag, isGenerating,searchValue);
    }


    @Operation(summary = "노트 생성")
    @PostMapping("/note")
    public ScriptDto createNoteScript(@RequestBody ScriptDto scriptDto) {
        return service.createNoteScript(scriptDto);
    }


    @Operation(summary = "스크립트를 생성하고 시간을 계산하여 반환", description = "필수값 : ScriptDto ->"
            +"    \"title\",\n" +
            "    \"contents\",\n" +
            "    \"speed\",\n" )
    @PostMapping("/time")
    public ScriptDto createScriptAndGetTime(@RequestBody ScriptDto scriptDto) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        return service.createScriptAndGetTime(scriptDto);
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

    @Operation(summary = "스크립트 데이터 수정 (발표시간 재 계산 로직 o)",  description = "필수값 : ScriptDto ->"
            +"    \"title\",\n" +
            "    \"content\",\n" +
            "    \"speed\",\n" )
    @PutMapping("/{id}/cal")
    public ScriptDto updateScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateScript(id, scriptDto);
    }


    @Operation(summary = "스크립트 데이터 수정 (발표시간 재 계산 로직 x)", description = "원하는 필드값을 삽입해주세요")
    @PutMapping("/{id}/uncal")
    public ScriptDto updateUncalScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateUncalScript(id, scriptDto);
    }

    @DeleteMapping("/{id}")
    public void deleteScript(@PathVariable Long id) {
        service.deleteScript(id);
    }

}

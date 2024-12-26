package site.balpyo.script.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 모든 사용자가 자신의 스크립트 목록을 가져오는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(summary = "모든 스크립트 조회", description = "현재 로그인된 사용자가 작성한 모든 스크립트를 조회합니다. 스크립트는 ScriptDto로 반환됩니다.")
    @GetMapping
    public List<ScriptDto> getAllScripts() {
        return service.getAllScripts();
    }

    // 사용자가 특정 ID의 스크립트를 조회하는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(summary = "ID로 스크립트 조회", description = "현재 로그인된 사용자가 스크립트 ID를 통해 해당 스크립트를 조회합니다.")
    @GetMapping("/{id}")
    public ScriptDto getScriptById(@PathVariable Long id) {
        return service.getScriptById(id);
    }

    // 새로운 노트 스크립트를 생성하는 API
    @PostMapping("/note")
    @Operation(
            summary = "노트 스크립트 생성",
            description = "노트 형식의 스크립트를 생성합니다. 스크립트는 ScriptDto로 반환됩니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScriptDto.class),
                            examples = @ExampleObject(
                                    name = "Note Script Example",
                                    summary = "노트 스크립트 예시",
                                    value = """
                                    {
                                      "title": "회의 준비",
                                      "content": "내일 회의에서 발표할 자료 내용"
                                    }
                                    """
                            )
                    )
            )
    )
    public ScriptDto createNoteScript(@RequestBody ScriptDto scriptDto) {
        return service.createNoteScript(scriptDto);
    }

    // 새로운 노트 스크립트를 생성하는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(summary = "플로우 스크립트 생성", description = "플로우 형식의 스크립트를 생성합니다. 스크립트는 ScriptDto로 반환됩니다.")
    @PostMapping("/flow")
    public ScriptDto createFlowScript(@RequestBody ScriptDto scriptDto) {
        return service.createFlowScript(scriptDto);
    }

    // 스크립트를 생성하고 그 발표 시간을 계산하여 반환하는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
            summary = "스크립트 생성 및 발표 시간 계산",
            description = "스크립트를 생성하고 발표 시간을 계산하여 반환합니다. 필수 필드는 title, content, speed입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScriptDto.class),
                            examples = @ExampleObject(
                                    name = "Time Calculation Example",
                                    summary = "스크립트 및 발표 시간 계산 예시",
                                    value = "{\n" +
                                            "  \"title\": \"제품 발표\",\n" +
                                            "  \"content\": \"신제품 특징에 대한 발표 내용\",\n" +
                                            "  \"speed\": 1.0\n" +
                                            "}"
                            )
                    )
            )
    )
    @PostMapping("/time")
    public ScriptDto createScriptAndGetTime(@RequestBody ScriptDto scriptDto) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        return service.createScriptAndGetTime(scriptDto);
    }

    // AI를 통해 스크립트를 생성하고 저장하는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
            summary = "AI 스크립트 생성",
            description = "AI를 사용해 스크립트를 생성하고 이를 저장합니다. 필수 필드는 title, topic, keywords, secTime입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScriptDto.class),
                            examples = @ExampleObject(
                                    name = "AI Script Example",
                                    summary = "AI 스크립트 생성 예시",
                                    value = "{\n" +
                                            "  \"title\": \"AI 주제 발표\",\n" +
                                            "  \"topic\": \"인공지능의 미래\",\n" +
                                            "  \"keywords\": \"AI, 미래, 기술\",\n" +
                                            "  \"secTime\": 120,\n" +
                                            "  \"fcmToken\": \"필요하다면\"\n" +
                                            "}"

                            )
                    )
            )
    )
    @PostMapping("/generate")
    public ScriptDto generateAndSaveScript(@RequestBody ScriptDto scriptDto) {
        return aiService.generateAiScriptAndSave(scriptDto);
    }

    // 스크립트를 수정하고 발표 시간을 다시 계산하는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
            summary = "스크립트 수정 및 발표 시간 재 계산",
            description = "스크립트를 수정하고 발표 시간을 재 계산합니다. 필수 필드는 title, content, speed입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScriptDto.class),
                            examples = @ExampleObject(
                                    name = "Update Script Example",
                                    summary = "스크립트 수정 예시",
                                    value = "{\n" +
                                            "  \"title\": \"발표 자료 업데이트\",\n" +
                                            "  \"content\": \"업데이트된 발표 자료 내용\",\n" +
                                            "  \"speed\": 2\n" +
                                            "}"
                            )
                    )
            )
    )
    @PutMapping("/{id}/cal")
    public ScriptDto updateScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateScript(id, scriptDto);
    }

    // 스크립트를 수정하지만 발표 시간은 재 계산하지 않는 API
    @PreAuthorize("hasRole('UNVERIFIED_USER') or hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Operation(
            summary = "스크립트 수정 (발표 시간 계산 없음)",
            description = "스크립트를 수정하지만 발표 시간을 다시 계산하지 않습니다. 원하는 필드값만 수정하여 입력하면 됩니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScriptDto.class),
                            examples = @ExampleObject(
                                    name = "Partial Update Example",
                                    summary = "발표 시간 계산 없는 수정 예시",
                                    value =  """
                                    {
                                      "title": "수정된 타이틀",
                                      "content": "내일 회의에서 발표할 자료 내용이 수정되었음"
                                    }
                                    """
                            )
                    )
            )
    )
    @PutMapping("/{id}/uncal")
    public ScriptDto updateUncalScript(@PathVariable Long id, @RequestBody ScriptDto scriptDto) {
        return service.updateUncalScript(id, scriptDto);
    }


    @GetMapping("/search")
    public List<ScriptDto> searchScripts(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean isGenerating,
            @RequestParam(required = false) String searchValue) {
        return service.getAllScriptByTagAndIsGenerating(tag, isGenerating, searchValue);
    }
}

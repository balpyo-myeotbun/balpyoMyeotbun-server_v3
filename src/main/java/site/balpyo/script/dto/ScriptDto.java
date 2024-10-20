package site.balpyo.script.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import site.balpyo.script.entity.Script;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


import static site.balpyo.script.service.SpeechMarkUtil.convertSpeechMarksToString;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ScriptDto {

    private Long id;
    private String content;
    private String title;
    private Integer secTime;
    private String voiceFilePath;
    private Boolean isGenerating;
    private String filePath;
    private Integer playTime;
    private List<Map<String, Object>> speechMark;
    private String originalScript; // 원본대본
    private Integer speed; // Speed adjustment [-2, -1, 0, 1, 2]
    private boolean useAi;
    private List<String> tags;
    private String topic;
    private String keywords;
    private String fcmToken;
    private String profileUrl;
    private InputStream audioStream;


    public Script toEntity() {

        Script script = new Script();
        script.setId(this.id);
        script.setContent(this.content);
        script.setTitle(this.title);
        script.setSecTime(this.secTime);
        script.setVoiceFilePath(this.voiceFilePath);
        script.setIsGenerating(this.isGenerating);
        script.setFilePath(this.filePath);
        script.setPlayTime(this.playTime);
        script.setSpeechMark(convertSpeechMarksToString(this.speechMark));
        script.setOriginalScript(this.originalScript);
        script.setSpeed(this.speed);
        script.setUseAi(this.useAi);
        script.setTags(this.tags != null ? String.join(",", this.tags) : null); // Converting list back to comma-separated values
        script.setTopic(this.topic);
        script.setKeywords(this.keywords);
        script.setFcmToken(this.fcmToken);
        script.setProfileUrl(this.profileUrl);

        return script;
    }

}

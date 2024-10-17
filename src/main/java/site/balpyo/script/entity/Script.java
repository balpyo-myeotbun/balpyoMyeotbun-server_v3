package site.balpyo.script.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import site.balpyo.auth.entity.User;
import site.balpyo.script.dto.ScriptDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scriptId;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String title;

    private Integer secTime;

    private String voiceFilePath;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Boolean isGenerating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String filePath;


    private Integer playTime;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String speechMark;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String originalScript; // 원본대본


    private Integer speed; // Speed adjustment [-2, -1, 0, 1, 2]


    private boolean useAi;

    private String tags;

    private String topic;

    private String keywords;
    private String fcmToken;
    private String profileUrl;

    public ScriptDto toDto() {
        ScriptDto scriptDto = new ScriptDto();
        scriptDto.setScriptId(this.scriptId);
        scriptDto.setContent(this.content);
        scriptDto.setTitle(this.title);
        scriptDto.setSecTime(this.secTime);
        scriptDto.setVoiceFilePath(this.voiceFilePath);
        scriptDto.setIsGenerating(this.isGenerating);
        scriptDto.setFilePath(this.filePath);
        scriptDto.setPlayTime(this.playTime);
        scriptDto.setSpeechMark(this.speechMark);
        scriptDto.setOriginalScript(this.originalScript);
        scriptDto.setSpeed(this.speed);
        scriptDto.setUseAi(this.useAi);
        scriptDto.setTags(this.tags != null ? List.of(this.tags.split(",")) : null); // Assuming tags are stored as comma-separated values
        scriptDto.setTopic(this.topic);
        scriptDto.setKeywords(this.keywords);
        scriptDto.setFcmToken(this.fcmToken);
        scriptDto.setProfileUrl(this.profileUrl);

        return scriptDto;
    }

}

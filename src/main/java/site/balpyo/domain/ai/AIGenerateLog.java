package site.balpyo.domain.ai;

import jakarta.persistence.*;
import site.balpyo.domain.script.Script;
import site.balpyo.global.BaseEntity;

@Entity
@Table(name = "AI_GENERATE_LOG")
public class AIGenerateLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AI_GENERATE_LOG_NO", nullable = false)
    private Long aiGenerateLogNo;

    @Column(name = "SEC_TIME", nullable = false)
    private Double secTime;

    @Column(name = "KEYWORDS", length = 255)
    private String keywords;

    @Column(name = "SEC_PER_LETTER", nullable = false)
    private Double secPerLetter;

    @ManyToOne
    @JoinColumn(name = "SCRIPT_NO", nullable = false)
    private Script script;

}

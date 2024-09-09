package site.balpyo.domain.user;
import jakarta.persistence.*;
import site.balpyo.global.BaseEntity;

@Entity
@Table(name = "USER")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_NO", nullable = false)
    private Long userNo;

    @Column(name = "USER_NICKNAME", nullable = false, length = 255)
    private String userNickname;

    @Column(name = "USER_LEVEL", nullable = false, length = 255)
    private String userLevel;

    @Column(name = "USER_TYPE", length = 255)
    private String userType;

    @Column(name = "USER_EMAIL", length = 255)
    private String userEmail;

    // Getters and setters
}

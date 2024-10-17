package site.balpyo.auth;

import org.springframework.stereotype.Component;


@Component
public class EmailConfig {

    private static final String BALPYO_TITLE = "[발표몇분] 회원가입 인증코드";

    private static final String SUPPORT_EMAIL = "balpyohelper@gmail.com";

    public String getBalpyoTitle() {
        return BALPYO_TITLE;
    }

    public String getBalpyoBody(String link) {
        return new StringBuilder()
                .append("안녕하세요,\n")
                .append("발표몇분입니다.\n")
                .append("\n")
                .append("아래 버튼으로 이메일 인증을 해주세요.\n")
                .append(link).append("\n")
                .append("\n")
                .append("감사합니다,\n")
                .append("발표몇분 드림\n")
                .append("\n")
                .append("클릭으로 인증이 되지 않는다면,\n")
                .append("위의 긴 주소를 인터넷 브라우저 주소 창에 붙여 넣어 보세요!\n")
                .append("계속 인증에 실패한다면 ").append(SUPPORT_EMAIL).append("을 통해 저희에게 연락 부탁 드립니다!")
                .toString();
    }
}

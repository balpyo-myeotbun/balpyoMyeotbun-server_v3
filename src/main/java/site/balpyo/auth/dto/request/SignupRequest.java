package site.balpyo.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    private String username;

    @NotBlank(message = "이메일은 공백일 수 없습니다")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다")
    private String password;


}

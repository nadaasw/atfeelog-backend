package hello.atfeelogbackend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserInput {
    //input CreateUserInput {
    //  email: String!
    //  password: String!
    //  name: String!
    //}

    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(
            // 대문자 + 소문자 + 숫자 + 특수문자 각 1개 이상, 공백 불가, 8~15자
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])(?=\\S+$).{8,15}$",
            message = "비밀번호는 8~15자이며 대문자·소문자·숫자·특수문자(@$!%*#?&)를 각각 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    @Size(min = 4, max = 20, message = "닉네임 형식은 4자이상 20자이하입니다.")
    private String name;

}

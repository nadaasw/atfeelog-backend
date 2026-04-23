package hello.atfeelogbackend.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}

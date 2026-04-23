package hello.atfeelogbackend.domain.user.dto;

import lombok.Getter;

@Getter
public class TokenDto {

    private String accessToken;

    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
    }


}

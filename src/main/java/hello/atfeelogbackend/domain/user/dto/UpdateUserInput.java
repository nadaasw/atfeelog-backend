package hello.atfeelogbackend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInput {
    private String name;
    private String password;
    private String picture;

    public UpdateUserInput(String name,String password,  String picture) {
        this.name = name;
        this.password = password;
        this.picture = picture;
    }
}

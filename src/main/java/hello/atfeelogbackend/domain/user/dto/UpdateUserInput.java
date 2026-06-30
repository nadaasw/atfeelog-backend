package hello.atfeelogbackend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInput {
    private String name;
    private String password;
    private String picture;
    private String description;

    public UpdateUserInput(String name,String password,  String picture, String description) {
        this.name = name;
        this.password = password;
        this.picture = picture;
        this.description = description;
    }
}

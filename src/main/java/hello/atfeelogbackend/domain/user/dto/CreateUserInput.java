package hello.atfeelogbackend.domain.user.dto;

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

    private String email;
    private String password;
    private String name;

}

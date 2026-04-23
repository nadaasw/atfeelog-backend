package hello.atfeelogbackend.domain.user.dto;

import hello.atfeelogbackend.domain.user.entity.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String picture;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserDto(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.picture = user.getPicture();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}

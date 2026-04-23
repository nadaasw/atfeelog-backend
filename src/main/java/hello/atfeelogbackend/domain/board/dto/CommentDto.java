package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.BoardComment;
import hello.atfeelogbackend.domain.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class CommentDto {
    private Long id;
    private String content;
    private UserDto user;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public CommentDto(BoardComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = new UserDto(comment.getUser());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}

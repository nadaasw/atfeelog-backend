package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public class FetchBoardResponse {
    private Long id;
    private UserDto user;
    private String artist;
    private String concert;
    private String contents;
    private BoardAddressDto boardAddress;
    private List<String> images;
    private int commentCount;
    private int likeCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public FetchBoardResponse(Board boar) {
        this.id = boar.getId();
        this.user = new UserDto(boar.getUser());
        this.artist = boar.getArtist();
        this.concert = boar.getConcert();
        this.contents = boar.getContents();
        this.boardAddress = boar.getBoardAddress() != null ? new BoardAddressDto(boar.getBoardAddress()) : null;
        this.images = boar.getImages();
        this.commentCount = boar.getComments() == null ? 0 : boar.getComments().size();
        this.likeCount = boar.getLikes() == null ? 0 : boar.getLikes().size();
        this.createdAt = boar.getCreatedAt();
        this.updatedAt = boar.getUpdatedAt();
    }

}

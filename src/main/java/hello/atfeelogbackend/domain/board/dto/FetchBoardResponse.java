package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class FetchBoardResponse {
    private Long id;
    private UserDto user;
    private String title;
    private String artistName;
    private String showName;
    private OffsetDateTime showDate;
    private String contents;
    private BoardAddressDto boardAddress;
    private List<String> images;
    private int commentCount;
    private int likeCount;
    private boolean isLiked = false;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public FetchBoardResponse(Board boar) {
        this.id = boar.getId();
        this.user = new UserDto(boar.getUser());
        this.title = boar.getTitle();
        this.artistName = boar.getArtistName();
        this.showName = boar.getShowName();
        this.showDate = boar.getShowDate();
        this.contents = boar.getContents();
        this.boardAddress = boar.getBoardAddress() != null ? new BoardAddressDto(boar.getBoardAddress()) : null;
        this.images = boar.getImages();
        this.commentCount = boar.getComments() == null ? 0 : boar.getComments().size();
        this.likeCount = boar.getLikes() == null ? 0 : boar.getLikes().size();
        this.createdAt = boar.getCreatedAt();
        this.updatedAt = boar.getUpdatedAt();
    }


    public FetchBoardResponse(Board boar, boolean isLiked) {
        this.id = boar.getId();
        this.user = new UserDto(boar.getUser());
        this.title = boar.getTitle();
        this.artistName = boar.getArtistName();
        this.showName = boar.getShowName();
        this.showDate = boar.getShowDate();
        this.contents = boar.getContents();
        this.boardAddress = boar.getBoardAddress() != null ? new BoardAddressDto(boar.getBoardAddress()) : null;
        this.images = boar.getImages();
        this.commentCount = boar.getComments() == null ? 0 : boar.getComments().size();
        this.likeCount = boar.getLikes() == null ? 0 : boar.getLikes().size();
        this.createdAt = boar.getCreatedAt();
        this.updatedAt = boar.getUpdatedAt();
        this.isLiked = isLiked;
    }
}

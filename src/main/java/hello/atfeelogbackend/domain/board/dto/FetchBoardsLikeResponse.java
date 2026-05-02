package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class FetchBoardsLikeResponse {
    private Long id;
    private UserDto user;
    private String title;
    private String artistName;
    private String showName;
    private OffsetDateTime showDate;
    private String contents;
    private BoardAddressDto boardAddress;
    private Boolean isLiked;
    private List<String> images;
    private int commentCount;
    private int likeCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public FetchBoardsLikeResponse(Board board,boolean isLiked) {
        this.id = board.getId();
        this.user = new UserDto(board.getUser());
        this.title = board.getTitle();
        this.artistName = board.getArtistName();
        this.showName = board.getShowName();
        this.showDate = board.getShowDate();
        this.contents = board.getContents();
        this.boardAddress = new BoardAddressDto(board.getBoardAddress());
        this.isLiked = isLiked;
        this.images = board.getImages();
        this.commentCount = board.getComments().size();
        this.likeCount = board.getLikes().size();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }
}

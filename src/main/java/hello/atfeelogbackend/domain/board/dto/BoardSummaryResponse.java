package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.user.dto.UserDto;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class BoardSummaryResponse {
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
    private boolean isLiked;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BoardSummaryResponse(
            Board board,
            int commentCount,
            int likeCount,
            boolean isLiked
    ) {
        this.id = board.getId();
        this.user = new UserDto(board.getUser());
        this.title = board.getTitle();
        this.artistName = board.getArtistName();
        this.showName = board.getShowName();
        this.showDate = board.getShowDate();
        this.contents = board.getContents();
        this.boardAddress = board.getBoardAddress() != null
                ? new BoardAddressDto(board.getBoardAddress())
                : null;
        this.images = board.getImages();
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }
}

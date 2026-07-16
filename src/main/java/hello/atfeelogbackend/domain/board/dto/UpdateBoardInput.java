package hello.atfeelogbackend.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateBoardInput {
    private String title;
    private String artistName;
    private String showName;
    private String contents;
    private OffsetDateTime showDate;
    private BoardAddressInput boardAddressInput;
    private List<String> images;
    private String mt20id;
    private String posterUrl;
    private String genre;
}

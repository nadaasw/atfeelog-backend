package hello.atfeelogbackend.domain.board.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class CreateBoardInput {
    private String title;
    private String showName;
    private String artistName;
    private String contents;
    private OffsetDateTime showDate;
    private BoardAddressInput boardAddressInput;
    private List<String> images;
    private String mt20id;
    private String posterUrl;
    private String genre;
}

package hello.atfeelogbackend.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateBoardInput {

    // title: String
    //  contents: String
    //  youtubeUrl: String
    //  boardAddress: BoardAddressInput
    //  images: [String!]

    private String artist;
    private String concertName;
    private String contents;
    private BoardAddressInput boardAddressInput;
    private List<String> images;
}

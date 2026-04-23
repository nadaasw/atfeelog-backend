package hello.atfeelogbackend.domain.board.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateBoardInput {
    //  writer: String
    //  password: String
    //  title: String!
    //  contents: String!
    //  youtubeUrl: String
    //  boardAddress: BoardAddressInput
    //  images: [String!]

    private String concertName;
    private String artist;
    private String contents;
    private BoardAddressInput boardAddressInput;
    private List<String> images;
}

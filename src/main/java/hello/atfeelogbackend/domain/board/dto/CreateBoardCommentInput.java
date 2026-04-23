package hello.atfeelogbackend.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardCommentInput {
    // input UpdateBoardCommentInput {
    //  contents: String
    //  rating: Float
    //}

    private String content;

}

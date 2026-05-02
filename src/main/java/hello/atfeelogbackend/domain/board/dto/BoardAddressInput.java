package hello.atfeelogbackend.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardAddressInput {
    // zipcode: String
    //  address: String
    //  addressDetail: String

    private String placeName;
    private String jibunAddress;
    private String roadAddress;
    private String x;
    private String y;
}

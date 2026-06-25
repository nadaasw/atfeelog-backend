package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.BoardAddress;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class BoardAddressDto {
    private Long id;
    private String placeName;
    private String jibunAddress;
    private String roadAddress;
    private String x;
    private String y;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BoardAddressDto(BoardAddress boardAddress) {
        this.id = boardAddress.getId();
        this.placeName = boardAddress.getPlaceName();
        this.jibunAddress = boardAddress.getJibunAddress();
        this.roadAddress = boardAddress.getRoadAddress();
        this.x = boardAddress.getX();
        this.y = boardAddress.getY();
        this.createdAt = boardAddress.getCreatedAt();
        this.updatedAt = boardAddress.getUpdatedAt();
    }
}

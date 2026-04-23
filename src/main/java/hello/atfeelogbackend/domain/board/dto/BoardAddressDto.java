package hello.atfeelogbackend.domain.board.dto;

import hello.atfeelogbackend.domain.board.entity.BoardAddress;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class BoardAddressDto {
    private Long id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String x;
    private String y;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BoardAddressDto(BoardAddress boardAddress) {
        this.id = boardAddress.getId();
        this.placeName = boardAddress.getPlaceName();
        this.addressName = boardAddress.getAddressName();
        this.roadAddressName = boardAddress.getRoadAddressName();
        this.x = boardAddress.getX();
        this.y = boardAddress.getY();
        this.createdAt = boardAddress.getCreatedAt();
        this.updatedAt = boardAddress.getUpdatedAt();
    }
}

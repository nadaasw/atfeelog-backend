package hello.atfeelogbackend.domain.item.entity;


import hello.atfeelogbackend.domain.board.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Table(name = "item_address")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String placeName;

    private String jibunAddress;

    private String roadAddress;

    private String x;

    private String y;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public ItemAddress(String placeName, String jibunAddress, String roadAddress, String x, String y) {
        this.placeName = placeName;
        this.jibunAddress = jibunAddress;
        this.roadAddress = roadAddress;
        this.x = x;
        this.y = y;
    }

    public void update(String placeName, String jibunAddress, String roadAddress, String x, String y) {
        this.placeName = placeName;
        this.jibunAddress = jibunAddress;
        this.roadAddress = roadAddress;
        this.x = x;
        this.y = y;
    }
}

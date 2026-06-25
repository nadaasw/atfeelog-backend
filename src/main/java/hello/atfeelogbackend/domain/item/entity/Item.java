package hello.atfeelogbackend.domain.item.entity;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer price;

    private List<String> tags;

    private boolean sold = false;

    private List<String> images;

    private int pickedCount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemAddress itemAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;


    public Item(String name, String description, Integer price, List<String> tags, List<String> images,ItemAddress itemAddress, User seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.tags = tags;
        this.images = images;
        this.seller = seller;
        this.itemAddress = itemAddress;
    }

    public Item update(String name, String description, Integer price, List<String> tags, List<String> images){
        if(name != null) this.name = name;
        if(description != null) this.description = description;
        if(price != null) this.price = price;
        if(tags != null) this.tags = tags;
        if(images != null) this.images = images;
        return this;
    }

    public Item clear(User buyer){
        this.sold = true;
        this.buyer = buyer;

        return this;
    }



}

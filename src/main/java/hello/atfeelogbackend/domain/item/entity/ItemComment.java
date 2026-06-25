package hello.atfeelogbackend.domain.item.entity;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Table(name = "item_comment")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public ItemComment(Item item, String content, User parent, User writer) {
        this.item = item;
        this.content = content;
        if(parent != null) this.parent = parent;
        this.writer = writer;
    }

    public ItemComment update(String content){ this.content = content; return this; }


}

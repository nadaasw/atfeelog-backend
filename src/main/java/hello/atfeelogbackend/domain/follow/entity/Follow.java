package hello.atfeelogbackend.domain.follow.entity;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "follows",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
        }
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;  // 팔로우 당하는 사람 (대상)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;  // 팔로우 하는 사람 (나)

    @CreatedDate
    private OffsetDateTime createdAt;

    @Builder
    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }
}

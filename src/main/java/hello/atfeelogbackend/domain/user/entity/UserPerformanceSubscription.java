package hello.atfeelogbackend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "user_performance_subscription",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_mt20id",
                        columnNames = {"user_id", "mt20id"}
                )
        }
)
public class UserPerformanceSubscription {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mt20id", length = 20, nullable = false)
    private String mt20id;

    @CreatedDate
    private LocalDateTime createdAt;


}

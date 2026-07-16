package hello.atfeelogbackend.domain.user.repository;

import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.entity.UserPerformanceSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPerformanceSubscriptionRepository extends JpaRepository<UserPerformanceSubscription, UUID> {
    Optional<UserPerformanceSubscription> findByUserAndMt20id(User user, String mt20id);

    Optional<List<UserPerformanceSubscription>> findAllByUser(User user);
}

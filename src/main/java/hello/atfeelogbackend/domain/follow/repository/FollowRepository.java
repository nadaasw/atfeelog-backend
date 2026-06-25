package hello.atfeelogbackend.domain.follow.repository;

import hello.atfeelogbackend.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    Integer countByFollowerId(Long followerId);

    Integer countByFolloweeId(Long followeeId);

    List<Follow> findAllByFolloweeId(Long followeeId);

    List<Follow> findAllByFollowerId(Long followerId);

    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

}

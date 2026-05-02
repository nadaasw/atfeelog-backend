package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.BoardLike;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    int countByBoardId(Long boardId); // 좋아요 개수
    boolean existsByBoardIdAndUserId(Long boardId, Long userId); // 좋아요 했는지 확인
    void deleteByBoardIdAndUserId(Long boardId, Long userId); // 좋아요 취소
    List<BoardLike> findByUserId(Long userId);

    @Query("select bl FROM BoardLike bl Join FETCH bl.board WHERE bl.user.id = :userId")
    List<BoardLike> findByUserIdWithBoard(@Nonnull Long userId);
}

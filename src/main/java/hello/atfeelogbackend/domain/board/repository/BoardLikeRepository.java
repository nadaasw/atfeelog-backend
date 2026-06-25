package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.BoardLike;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    int countByBoardId(Long boardId); // 좋아요 개수
    int countByUserId(Long userId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId); // 좋아요 했는지 확인
    void deleteByBoardIdAndUserId(Long boardId, Long userId); // 좋아요 취소
    List<BoardLike> findByUserId(Long userId);

    @Query("select bl FROM BoardLike bl Join FETCH bl.board WHERE bl.user.id = :userId")
    List<BoardLike> findByUserIdWithBoard(@Nonnull Long userId);

    @Query("""
    select l.board.id
    from BoardLike l
    where l.user.id = :userId
    and l.board.id in :boardIds
    """)
    List<Long> findLikedBoardIds(Long userId, List<Long> boardIds);


    @Query("""
          select l.board.id, count(l)
          from BoardLike l
          where l.board.id in :boardIds
          group by l.board.id
      """)
    List<Object[]> countByBoardIds(@Param("boardIds") List<Long> boardIds);
}

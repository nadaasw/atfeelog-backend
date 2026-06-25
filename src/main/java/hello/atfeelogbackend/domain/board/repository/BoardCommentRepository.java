package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    public Page<BoardComment>findByBoardId(Long boardId, Pageable pageable);

    @Query("""
          select c.board.id, count(c)
          from BoardComment c
          where c.board.id in :boardIds
          group by c.board.id
      """)
    List<Object[]> countByBoardIds(@Param ("boardIds") List<Long> boardIds);
}

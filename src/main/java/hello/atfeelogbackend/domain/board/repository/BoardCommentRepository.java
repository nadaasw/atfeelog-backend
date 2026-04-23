package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    public Page<BoardComment>findByBoardId(Long boardId, Pageable pageable);
}

package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE " +
            "(b.concert LIKE %:search% OR b.artist LIKE %:search%) " +
            "AND (:start IS NULL OR b.createdAt >= :start) " +
            "AND (:end IS NULL OR b.createdAt <= :end)")
    Page<Board> searchBoards(
            @Param("search") String search,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    List<Board> findAllByUserId(Long userId);

}

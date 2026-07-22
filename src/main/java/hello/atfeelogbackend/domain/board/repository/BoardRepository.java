package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    int countByUserId(Long userId);

    @Query(value = "SELECT b FROM Board b " +
            "LEFT JOIN  fetch  b.boardAddress " +
            "WHERE (:search IS NULL OR b.showName LIKE %:search% OR b.artistName LIKE %:search% OR b.title LIKE %:search% OR b.contents LIKE %:search%) " +
            "AND b.createdAt >= COALESCE(:start, b.createdAt) " +
            "AND b.createdAt <= COALESCE(:end, b.createdAt) " +
            "ORDER BY b.createdAt DESC ",
            countQuery = "SELECT COUNT(b) FROM Board b " +
                    "WHERE (:search IS NULL OR b.showName LIKE %:search% OR b.artistName LIKE %:search% OR b.title LIKE %:search% OR b.contents LIKE %:search%) " +
                    "AND b.createdAt >= COALESCE(:start, b.createdAt) " +
                    "AND b.createdAt <= COALESCE(:end, b.createdAt)")
    Page<Board> searchBoards(
            @Param("search") String search,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            Pageable pageable
    );

    @Query("SELECT b FROM  Board b " +
            "JOIN fetch b.user " +
            "where b.mt20id = :mt20id " +
            "order by b.createdAt DESC ")
    Page<Board> getBoardsByMy20id(String mt20id, Pageable pageable);
//    @Query(value = "SELECT b FROM Board b " +
//            "JOIN FETCH b.user " +
//            "LEFT JOIN FETCH b.boardAddress " +
//            "WHERE (:search IS NULL OR b.showName LIKE %:search% OR b.artistName LIKE %:search% OR b.title LIKE %:search% OR b.contents LIKE %:search%) " +
//            "AND (:start IS NULL OR b.createdAt >= :start) " +
//            "AND (:end IS NULL OR b.createdAt <= :end)",
//            countQuery = "SELECT COUNT(b) FROM Board b " +
//                    "WHERE (:search IS NULL OR b.showName LIKE %:search% OR b.artistName LIKE %:search% OR b.title LIKE %:search% OR b.contents LIKE %:search%) " +
//                    "AND (:start IS NULL OR b.createdAt >= :start) " +
//                    "AND (:end IS NULL OR b.createdAt <= :end)")
//    Page<Board> searchBoards1(
//            @Param("search") String search,
//            @Param("start") OffsetDateTime start,
//            @Param("end") OffsetDateTime end,
//            Pageable pageable
//    );

    @Query("SELECT b FROM Board b ORDER BY size(b.likes) DESC, b.createdAt DESC")
    Page<Board> findBoardsOfBest(Pageable pageable);

    List<Board> findAllByUserId(Long userId);


    // N+1 문제 해결 시도
    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.user " +
            "LEFT JOIN  fetch b.images " +
            "WHERE b.id = :id"
    )
    Optional<Board> fetchBoardById(@Param("id") Long id);


    // 인기 키워드
    @Query(value = "SELECT keyword FROM (" +
            " SELECT  artist_name as keyword FROM board " +
            " UNION ALL " +
            " SELECT show_name as keyword FROM board" +
            ") combined " +
            "GROUP BY keyword " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 5",
            nativeQuery = true)
    List<String> findTopKeywords();

    // 팔로잉한 사람들 글 가져오기
    @Query("""
        select b
        from Board b
        where b.user.id in (
            select f.follower.id
            from Follow f
            where f.followee.id = :userId
        )
        order by b.createdAt desc
    """)
    Page<Board> findAllByFollowing(@Param("userId") Long userId, Pageable pageable );

}

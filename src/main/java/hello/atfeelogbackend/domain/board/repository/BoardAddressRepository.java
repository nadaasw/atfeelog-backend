package hello.atfeelogbackend.domain.board.repository;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.board.entity.BoardAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardAddressRepository extends JpaRepository<BoardAddress, Long> {
    Optional<BoardAddress> findByPlaceName(String placeName);

    BoardAddress findByBoard(Board board);
}

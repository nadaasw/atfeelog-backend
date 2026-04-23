package hello.atfeelogbackend.domain.board.service;

import hello.atfeelogbackend.domain.board.dto.*;
import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.board.entity.BoardAddress;
import hello.atfeelogbackend.domain.board.entity.BoardComment;
import hello.atfeelogbackend.domain.board.entity.BoardLike;
import hello.atfeelogbackend.domain.board.repository.BoardAddressRepository;
import hello.atfeelogbackend.domain.board.repository.BoardCommentRepository;
import hello.atfeelogbackend.domain.board.repository.BoardLikeRepository;
import hello.atfeelogbackend.domain.board.repository.BoardRepository;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.service.UserService;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardAddressRepository boardAddressRepository;

    private final UserService userService;

    @Transactional
    public Board save(CreateBoardInput createBoardInput, Long userId) {

        User user = userService.findById(userId);

        Board board = boardRepository.save(
                Board.builder()
                        .artist(createBoardInput.getArtist())
                        .user(user)
                        .concert(createBoardInput.getConcertName())
                        .contents(createBoardInput.getContents())
                        .build()
        );

        BoardAddressInput boardAddressInput = createBoardInput.getBoardAddressInput();

        if (boardAddressInput != null) {
            BoardAddress boardAddress = BoardAddress.builder()
                    .placeName(boardAddressInput.getPlaceName())
                    .addressName(boardAddressInput.getAddressName())
                    .roadAddressName(boardAddressInput.getRoadAddressName())
                    .x(boardAddressInput.getX())
                    .y(boardAddressInput.getY())
                    .board(board)
                    .build();

            boardAddressRepository.save(boardAddress);
        }

        return board;
    }

    public Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("NO DATA FOUND"));
    }

    @Transactional
    public Board update(Long boardId, UpdateBoardInput updateBoardInput) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("NO DATA FOUND"));

        if(updateBoardInput.getBoardAddressInput() != null) {
            BoardAddress boardAddress = boardAddressRepository.findByBoard(board);
            BoardAddressInput boardAddressInput = updateBoardInput.getBoardAddressInput();
            boardAddress.update(boardAddressInput.getPlaceName(), boardAddressInput.getAddressName(), boardAddressInput.getRoadAddressName(), boardAddressInput.getX(), boardAddressInput.getY());
        }

        board.update(updateBoardInput.getArtist(), updateBoardInput.getConcertName(), updateBoardInput.getContents(), updateBoardInput.getImages());

        return board;
    }

    public Long delete(Long id, Long userId) {
        Board board = findById(id);
        if(!board.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없는 요청입니다.");
        }
        boardRepository.deleteById(id);
        return id;
    }

    public List<FetchBoardResponse> fetchBoards(LocalDateTime start, LocalDateTime end, String search, Integer page) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, 10,
                Sort.by("createdAt").descending());

        List<Board> temp = boardRepository.searchBoards(search, start, end, pageable).getContent();
        List<FetchBoardResponse> fetchBoardResponses = new ArrayList<>();
        for (Board board : temp) {
            fetchBoardResponses.add(new FetchBoardResponse(board));
        }

        return fetchBoardResponses;
    }

    public int fetchBoardsCount(LocalDateTime start, LocalDateTime end, String search) {
        Page<Board> page = boardRepository.searchBoards(search, start, end, PageRequest.of(0, 1));
        return (int) page.getTotalElements();
    }

    public List<FetchBoardResponse> fetchBoardOfMine(Long userId){
        List<Board> boards = boardRepository.findAllByUserId(userId);
        List<FetchBoardResponse> fetchBoardResponses = new ArrayList<>();
        for (Board board : boards) {
            fetchBoardResponses.add(new FetchBoardResponse(board));
        }
        return fetchBoardResponses;
    }

    public int fetchBoardOfMineCount(Long userId){
        List<Board> boards = boardRepository.findAllByUserId(userId);
        return boards.size();
    }

    public List<CommentDto> fetchComments(Long boardId, Integer page) {
        Pageable pageable = PageRequest.of((page != null) ? (page - 1): 0, 10,
                Sort.by("createdAt").descending());

        List<BoardComment> comments =  boardCommentRepository.findByBoardId(boardId, pageable).getContent();
        List<CommentDto> commentDtos = new ArrayList<>();
        for (BoardComment comment : comments) {
            commentDtos.add(new CommentDto(comment));
        }
        return commentDtos;
    }


    @Transactional
    public int likeBoard(Long boardId, Long userId) {
        if(boardLikeRepository.existsByBoardIdAndUserId(boardId, userId)) {
            boardLikeRepository.deleteByBoardIdAndUserId(boardId, userId);
        }else{
            Board board = findById(boardId);
            User user = userService.findById(userId);
            boardLikeRepository.save(new BoardLike(board, user));
        }

        return boardLikeRepository.countByBoardId(boardId);
    }


    @Transactional
    public BoardComment createComment(CreateBoardCommentInput input, Long boardId, Long userId) {
        Board board = findById(boardId);
        User user = userService.findById(userId);
        BoardComment comment = new BoardComment(input.getContent(), user, board);
        return boardCommentRepository.save(comment);
    }

    public Long deleteComment(Long commentId, Long userId) {
        BoardComment comment = boardCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("NO DATA FOUND"));
        if(!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }
        boardCommentRepository.delete(comment);
        return commentId;
    }


    @Transactional
    public CommentDto updateComment(Long commentId, String content, Long userId) {
        BoardComment comment = boardCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("NO DATA FOUND"));
        if(!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        comment.update(content);

        return new CommentDto(comment);
    }
}

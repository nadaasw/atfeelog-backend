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
import hello.atfeelogbackend.domain.follow.repository.FollowRepository;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.service.UserService;
import hello.atfeelogbackend.global.auth.CustomUserDetails;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardAddressRepository boardAddressRepository;

    private final UserService userService;

    private final FollowRepository followRepository;

    @Transactional
    public Board save(CreateBoardInput createBoardInput, Long userId) {

        User user = userService.findById(userId);

        Board board = boardRepository.save(
                Board.builder()
                        .title(createBoardInput.getTitle())
                        .artistName(createBoardInput.getArtistName())
                        .user(user)
                        .showName(createBoardInput.getShowName())
                        .contents(createBoardInput.getContents())
                        .showDate(createBoardInput.getShowDate())
                        .images(createBoardInput.getImages())
                        .build()
        );

        BoardAddressInput boardAddressInput = createBoardInput.getBoardAddressInput();

        if (boardAddressInput != null) {
            BoardAddress boardAddress = BoardAddress.builder()
                    .placeName(boardAddressInput.getPlaceName())
                    .jibunAddress(boardAddressInput.getJibunAddress())
                    .roadAddress(boardAddressInput.getRoadAddress())
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

    public boolean isLiked(Long id, Long userId) {
        return boardLikeRepository.existsByBoardIdAndUserId(id, userId);
    }

    public List<String> findTopKeyword(){
        return boardRepository.findTopKeywords();
    }

    @Transactional
    public Board update(Long boardId, UpdateBoardInput updateBoardInput) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("NO DATA FOUND"));

        if(updateBoardInput.getBoardAddressInput() != null) {
            BoardAddress boardAddress = boardAddressRepository.findByBoard(board);
            BoardAddressInput boardAddressInput = updateBoardInput.getBoardAddressInput();
            boardAddress.update(boardAddressInput.getPlaceName(), boardAddressInput.getJibunAddress(), boardAddressInput.getRoadAddress(), boardAddressInput.getX(), boardAddressInput.getY());
        }

        board.update(updateBoardInput.getTitle(), updateBoardInput.getArtistName(), updateBoardInput.getShowName(), updateBoardInput.getContents(), updateBoardInput.getImages(), updateBoardInput.getShowDate());

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

    public List<BoardSummaryResponse> fetchBoards(OffsetDateTime start, OffsetDateTime end, String search, Integer page, CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, 10,
                Sort.by("createdAt").descending());

        List<Board> boards = boardRepository
                .searchBoards(search, start, end, pageable)
                .getContent();

        List<Long> boardIds = boards.stream()
                .map(Board::getId)
                .toList();

        Map<Long, Integer> commentCountMap = boardCommentRepository
                .countByBoardIds(boardIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        Map<Long, Integer> likeCountMap = boardLikeRepository
                .countByBoardIds(boardIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        Set<Long> likedBoardIds = Set.of();

        if (customUserDetails != null && !boardIds.isEmpty()) {
            likedBoardIds = new HashSet<>(
                    boardLikeRepository.findLikedBoardIds(
                            customUserDetails.getUserId(),
                            boardIds
                    )
            );
        }

        Set<Long> finalLikedBoardIds = likedBoardIds;

        return boards.stream()
                .map(board -> new BoardSummaryResponse(
                        board,
                        commentCountMap.getOrDefault(board.getId(), 0),
                        likeCountMap.getOrDefault(board.getId(), 0),
                        finalLikedBoardIds.contains(board.getId())
                ))
                .toList();
    }

    public int fetchBoardsCount(OffsetDateTime start, OffsetDateTime end, String search) {
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
        return boardRepository.countByUserId(userId);
    }

    public List<FetchBoardsLikeResponse> fetchBoardsLike(Long userId){
        List<BoardLike> likes = boardLikeRepository.findByUserId(userId);
        List<FetchBoardsLikeResponse> fetchBoardsLikeResponses = new ArrayList<>();
        for(BoardLike boardLike : likes){
            Board board = boardLike.getBoard();
            fetchBoardsLikeResponses.add(new FetchBoardsLikeResponse(board, true));
        }

        return fetchBoardsLikeResponses;
    }

    public int fetchBoardsLikeCount(Long boardId){
        return boardLikeRepository.countByBoardId(boardId);
    }

    public List<FetchBoardResponse> fetchBoardsOfBest(Boolean isTop5, Integer page){

        Pageable pageable = PageRequest.of(0, 5);

        if(!isTop5) {
            pageable = PageRequest.of(page != null ? page - 1 : 0, 10);
        }

        List<Board> boards = boardRepository.findBoardsOfBest(pageable).getContent();

        List<FetchBoardResponse> fetchBoardResponses = new ArrayList<>();
        for (Board board : boards) {
            fetchBoardResponses.add(new FetchBoardResponse(board));
        }

        return fetchBoardResponses;
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

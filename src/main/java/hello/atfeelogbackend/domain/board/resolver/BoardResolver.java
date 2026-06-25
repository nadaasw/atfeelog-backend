package hello.atfeelogbackend.domain.board.resolver;

import hello.atfeelogbackend.domain.board.dto.*;
import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.board.service.BoardService;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardResolver {

    private final BoardService boardService;

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public FetchBoardResponse createBoard(@Argument CreateBoardInput createBoardInput, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        log.info(createBoardInput.getImages().toString());
        return new  FetchBoardResponse(boardService.save(createBoardInput, customUserDetails.getUserId()));
    }

    @QueryMapping
    public FetchBoardResponse fetchBoard(@Argument Long boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long userId = null;

        if(customUserDetails != null) {
            userId = customUserDetails.getUserId();
            return new FetchBoardResponse(boardService.findById(boardId), boardService.isLiked(boardId, userId));
        }
        return new FetchBoardResponse(boardService.findById(boardId));
    }

    @QueryMapping
    public List<BoardSummaryResponse> fetchBoards(@Argument OffsetDateTime endDate,
                                                @Argument OffsetDateTime startDate,
                                                @Argument String search,
                                                @Argument Integer page,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return boardService.fetchBoards(startDate, endDate, search, page, customUserDetails);
    }

    @QueryMapping
    public int fetchBoardsCount(@Argument OffsetDateTime endDate,
                                @Argument OffsetDateTime startDate,
                                @Argument String search){
        return boardService.fetchBoardsCount(startDate, endDate, search);
    }

    @QueryMapping
    public List<String> fetchBoardsKeyword(){
        return boardService.findTopKeyword();
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public int fetchBoardsCountOfMine(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getUserId();

        return boardService.fetchBoardOfMineCount(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<FetchBoardResponse> fetchBoardsOfMine(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getUserId();
        return boardService.fetchBoardOfMine(userId);
    }

    @QueryMapping
    public List<FetchBoardResponse> fetchBoardsByUser(@Argument Long userId){
        return boardService.fetchBoardOfMine(userId);
    }

    @QueryMapping
    public int fetchBoardsCountByUser(@Argument Long userId){
        return boardService.fetchBoardOfMineCount(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<FetchBoardsLikeResponse> fetchBoardsLike(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getUserId();
        return boardService.fetchBoardsLike(userId);
    }

    @QueryMapping
    public List<FetchBoardsLikeResponse> fetchBoardsLikeByUser(@Argument Long userId){
        return boardService.fetchBoardsLike(userId);
    }

    @QueryMapping
    public int fetchBoardsLikeCount(@Argument Long userId){
        return boardService.fetchBoardsLikeCount(userId);
    }

    @QueryMapping
    public List<CommentDto> fetchBoardComments(@Argument Integer page,@Argument Long boardId) {
        return boardService.fetchComments(boardId, page);
    }

    @QueryMapping
    public List<FetchBoardResponse> fetchBoardsOfBest(@Argument Boolean isTop5, @Argument Integer page){
        return boardService.fetchBoardsOfBest(isTop5, page);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public FetchBoardResponse updateBoard(@Argument UpdateBoardInput updateBoardInput, @Argument Long boardId) {
        return new FetchBoardResponse(boardService.update(boardId, updateBoardInput));
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Long deleteBoard(@Argument Long boardId ,@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return boardService.delete(boardId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public List<Long> deleteBoards(@Argument List<Long> boardIds, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        List<Long> deletedBoardIds = new ArrayList<>();
        for (Long boardId : boardIds) {
            try{
                deletedBoardIds.add(boardService.delete(boardId, userId));
            }catch (Exception e) {
                continue;
            }

        }
        return deletedBoardIds;
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public int likeBoard(@Argument Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return boardService.likeBoard(boardId, userId);
    }


    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public CommentDto createBoardComment(@Argument CreateBoardCommentInput createBoardCommentInput, @Argument Long boardId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return new CommentDto(boardService.createComment(createBoardCommentInput, boardId, userId));
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Long deleteBoardComment(@Argument Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return boardService.deleteComment(commentId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public CommentDto updateBoardComment(@Argument Long commentId, @Argument String content,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return boardService.updateComment(commentId, content, userId);
    }
}

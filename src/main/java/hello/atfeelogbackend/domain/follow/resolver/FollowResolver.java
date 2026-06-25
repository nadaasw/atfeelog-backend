package hello.atfeelogbackend.domain.follow.resolver;

import hello.atfeelogbackend.domain.board.dto.FetchBoardResponse;
import hello.atfeelogbackend.domain.follow.service.FollowService;
import hello.atfeelogbackend.domain.user.dto.UserDto;
import hello.atfeelogbackend.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FollowResolver {

    private final FollowService followService;

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    @Transactional
    public Boolean addFollow(@Argument Long followerId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUserId();

        followService.follow(followerId, userId);

        return true;
    }


    @QueryMapping
    public List<UserDto> fetchFollowers(@Argument Long userId) {
        return followService.getFollowers(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<UserDto> fetchFollowersOfMine(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return followService.getFollowers(userDetails.getUserId());
    }

    @QueryMapping
    public List<UserDto> fetchFollowings(@Argument Long userId) {
        return followService.getFollowing(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<UserDto> fetchFollowingOfMine(@AuthenticationPrincipal CustomUserDetails userDetails){
        return followService.getFollowing(userDetails.getUserId());
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public boolean isConnected(@Argument Long followerId, @AuthenticationPrincipal CustomUserDetails userDetails){
        return followService.isFollowing(followerId, userDetails.getUserId());
    }


    @QueryMapping
    public Integer fetchCountOfFollowers(@Argument Long userId) {
        return followService.countFollowers(userId);
    }


    @QueryMapping
    public Integer fetchCountOfFollowing(@Argument Long userId) {
        return followService.countFollowing(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<FetchBoardResponse> fetchFollowingFeed(@AuthenticationPrincipal CustomUserDetails userDetails, @Argument Integer page){
        Long userId = userDetails.getUserId();

        return followService.getFollowingFeed(userId, page);
    }

}

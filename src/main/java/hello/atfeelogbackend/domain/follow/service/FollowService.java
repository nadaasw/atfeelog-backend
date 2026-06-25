package hello.atfeelogbackend.domain.follow.service;

import hello.atfeelogbackend.domain.board.dto.FetchBoardResponse;
import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.board.repository.BoardRepository;
import hello.atfeelogbackend.domain.follow.entity.Follow;
import hello.atfeelogbackend.domain.follow.repository.FollowRepository;
import hello.atfeelogbackend.domain.user.dto.UserDto;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.repository.UserRepository;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // targetId: 팔로우할 대상(follower), userId: 나(followee)
    public void follow(Long targetId, Long userId) {
        if(targetId == null || userId == null || targetId.equals(userId)) {
            throw new CustomException(ErrorCode.FOLLOW_BY_SELF);
        }

        if(followRepository.existsByFollowerIdAndFolloweeId(targetId, userId)) {
            followRepository.deleteByFollowerIdAndFolloweeId(targetId, userId);
        } else {
            User follower = userRepository.getReferenceById(targetId);
            User followee = userRepository.getReferenceById(userId);

            Follow follow = Follow.builder()
                    .follower(follower)
                    .followee(followee)
                    .build();

            followRepository.save(follow);
        }
    }

    public List<FetchBoardResponse> getFollowingFeed(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, 10);
        Page<Board> list = boardRepository.findAllByFollowing(userId, pageable);
        return list.getContent().stream()
                .map(FetchBoardResponse::new)
                .toList();
    }

    // 나를 팔로우한 사람들 (follower_id = userId인 레코드 → followee가 나를 팔로우한 사람)
    public List<UserDto> getFollowers(Long userId) {
        List<Follow> follows = followRepository.findAllByFollowerId(userId);
        List<UserDto> users = new ArrayList<>();
        for(Follow follow : follows) {
            users.add(new UserDto(follow.getFollowee()));
        }
        return users;
    }

    // 내가 팔로우한 사람들 (followee_id = userId인 레코드 → follower가 내가 팔로우한 대상)
    public List<UserDto> getFollowing(Long userId) {
        List<Follow> follows = followRepository.findAllByFolloweeId(userId);
        List<UserDto> users = new ArrayList<>();
        for(Follow follow : follows) {
            users.add(new UserDto(follow.getFollower()));
        }
        return users;
    }

    // targetId: 확인할 대상, userId: 나
    public Boolean isFollowing(Long targetId, Long userId) {
        return followRepository.existsByFollowerIdAndFolloweeId(targetId, userId);
    }

    // 내가 팔로우한 수 (followee_id = userId)
    public Integer countFollowing(Long userId) {
        return followRepository.countByFolloweeId(userId);
    }

    // 나를 팔로우한 수 (follower_id = userId)
    public Integer countFollowers(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

}

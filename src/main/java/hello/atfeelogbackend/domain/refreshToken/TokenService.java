package hello.atfeelogbackend.domain.refreshToken;

import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.service.UserService;
import hello.atfeelogbackend.global.auth.TokenProvider;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import hello.atfeelogbackend.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final RedisService redisService;


    public String createNewAccessToken(String refreshToken) {
        if(!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(1));

    }

    public String createNewAccessTokenByRedis(String refreshToken) {
        if(!tokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = tokenProvider.getUserId(refreshToken);
        String storedRefreshToken = redisService.getValue(userId);
        if(storedRefreshToken == null ) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        if(!storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_MATCH);
        }
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(1));
    }

}

package hello.atfeelogbackend.domain.refreshToken;

import hello.atfeelogbackend.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisService redisService;
    private final RefreshTokenRepository refreshTokenRepository;



    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("No refresh token found"));
    }

    public RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId).orElseThrow(()-> new IllegalArgumentException("No User data"));
    }

    public boolean existsByUserId(Long userId) {
        return refreshTokenRepository.existsByUserId(userId);
    }


    // Redis

    public void saveRedis(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void saveRedisTtl(RefreshToken refreshToken) {
        redisService.setValueTtl(refreshToken.getUserId(), refreshToken.getRefreshToken(), 14);
    }

    public void deleteRedis(RefreshToken refreshToken) {
        redisService.delValue(refreshToken.getUserId());
    }

    public String findByUserIdByRedis(Long userId) {
        return redisService.getValue(userId);
    }



}

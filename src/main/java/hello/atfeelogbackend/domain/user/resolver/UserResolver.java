package hello.atfeelogbackend.domain.user.resolver;

import hello.atfeelogbackend.domain.refreshToken.RefreshToken;
import hello.atfeelogbackend.domain.refreshToken.RefreshTokenService;
import hello.atfeelogbackend.domain.refreshToken.TokenService;
import hello.atfeelogbackend.domain.user.dto.*;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.repository.UserRepository;
import hello.atfeelogbackend.domain.user.service.UserService;
import hello.atfeelogbackend.global.auth.CustomUserDetails;
import hello.atfeelogbackend.global.auth.TokenProvider;
import hello.atfeelogbackend.global.config.TokenAuthenticationFilter;
import hello.atfeelogbackend.global.cookie.CookieUtil;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.time.Duration;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public User fetchUserLoggedIn(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();

        return userService.findById(userId);
    }


    @Transactional
    @MutationMapping
    public LoginResponse loginUser(@Argument String email, @Argument String password) {


        // RequestContextHolder에서 꺼내기
        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getResponse();

        if(userService.validateUser(email, password)) {
            User user = userService.findByEmail(email);
            String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
            String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

            RefreshToken rf = refreshTokenService.existsByUserId(user.getId())
                    ? refreshTokenService.findByUserId(user.getId()).update(refreshToken)
                    : new RefreshToken(user.getId(), refreshToken);
            refreshTokenService.save(rf);


            CookieUtil.addCookie(response, "refreshToken", refreshToken, (int) REFRESH_TOKEN_DURATION.getSeconds());
         return new LoginResponse(accessToken);
        }

        throw new CustomException(ErrorCode.INVALID_INPUT);
    }

    @MutationMapping
    public UserDto createUser(@Argument CreateUserInput createUserInput){

        User user = userService.createUser(createUserInput);

        return new UserDto(user);
    }


    @MutationMapping
    public boolean logoutUser(@AuthenticationPrincipal CustomUserDetails principal, HttpServletRequest request) {
        try{
            Long userId = principal.getUserId();

            // RequestContextHolder에서 꺼내기
            HttpServletResponse response = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getResponse();

            RefreshToken refreshToken = refreshTokenService.findByUserId(userId);
            refreshTokenService.delete(refreshToken);
            CookieUtil.deleteCookie(request, response, "refreshToken");
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Transactional
    @MutationMapping
    public boolean resetUserPassword(@Argument String password, @AuthenticationPrincipal CustomUserDetails principal){
        try{
            Long userId = principal.getUserId();


            UpdateUserInput input = new UpdateUserInput(null, password, null, null);
            userService.updateUser(input, userId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Transactional
    @MutationMapping
    public User updateUser(@Argument UpdateUserInput updateUserInput, @AuthenticationPrincipal CustomUserDetails principal){

            Long userId = principal.getUserId();

            User user = userService.updateUser(updateUserInput, userId);
            return user;

    }

    @MutationMapping
    public TokenDto restoreAccessToken(){
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        String refreshToken = null;

        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    refreshToken = cookie.getValue();
                }
            }
        }

        if(refreshToken == null){
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        refreshTokenService.findByRefreshToken(refreshToken);

        String newToken = tokenService.createNewAccessToken(refreshToken);
        return new TokenDto(newToken);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean togglePerformanceSubscription(@Argument String mt20id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUserId();
        return userService.togglePerformanceSubscription(mt20id, userId);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<String> fetchSubscribedPerformances(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUserId();
        return userService.fetchSubscribedPerformances(userId);
    }
}

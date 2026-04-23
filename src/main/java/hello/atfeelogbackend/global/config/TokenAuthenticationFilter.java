package hello.atfeelogbackend.global.config;

import hello.atfeelogbackend.global.auth.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String AuthorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        String token = getAccessToken(AuthorizationHeader);
        log.info("[TokenFilter] header={}, token={}", AuthorizationHeader, token);

        if (token != null && tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("[TokenFilter] 인증 성공 - principal={}", auth.getPrincipal());
        } else {
            log.warn("[TokenFilter] 인증 실패 - token={}, valid={}", token, token != null && tokenProvider.validateToken(token));
        }
        filterChain.doFilter(request, response);
    }



    public String getAccessToken(String header) {
        if(header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

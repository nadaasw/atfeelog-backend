package hello.atfeelogbackend.global.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



public class CustomUserDetails  implements UserDetails {
    @Getter
    private final Long userId;
    private final String email;
    private final String authority = "ROLE_USER";

    public CustomUserDetails(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

}

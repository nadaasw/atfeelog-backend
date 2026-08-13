package hello.atfeelogbackend.domain.user.repository;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}

package hello.atfeelogbackend.domain.user.entity;

import hello.atfeelogbackend.domain.board.entity.Board;
import hello.atfeelogbackend.domain.board.entity.BoardComment;
import hello.atfeelogbackend.domain.board.entity.BoardLike;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BoardComment> boardCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BoardLike> boardLikeList = new ArrayList<>();

    private String email;

    private String password;

    private String name;

    private String picture;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public User(String email, String password, String name, String picture) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.picture = picture;
    }

    public void update(String name, String password, String picture){
        if(name != null) this.name = name;
        if(password != null) this.password = password;
        if(picture != null) this.picture = picture;
    }
}

package hello.atfeelogbackend.domain.board.entity;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "artist", nullable = false)
    private String artist;

    @Column(name = "concert", nullable = false)
    private String concert;

    @Column(name = "contents", nullable = false)
    private String contents;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes = new ArrayList<>();

    @OneToOne(mappedBy = "board")
    private BoardAddress boardAddress;

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public Board( String artist, User user, String concert, String contents, List<String> images) {
        this.artist = artist;
        this.user = user;
        this.concert = concert;
        this.contents = contents;
        this.images = images;
    }


    public void update(String artist, String concert, String contents, List<String> images) {
        if(artist != null) this.artist = artist;
        if(concert != null) this.concert = concert;
        if(contents != null) this.contents = contents;
        if(images != null) this.images = images;
    }
}

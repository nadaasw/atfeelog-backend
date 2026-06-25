package hello.atfeelogbackend.domain.board.entity;

import hello.atfeelogbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "show_name", nullable = false)
    private String showName;

    @Column(name = "show_date")
    private OffsetDateTime showDate;

    @Column(name = "contents", nullable = false)
    private String contents;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComment> comments = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes = new ArrayList<>();

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private BoardAddress boardAddress;

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public Board(String title, String artistName, User user, String showName, String contents, List<String> images, OffsetDateTime showDate) {
        this.title = title;
        this.artistName = artistName;
        this.user = user;
        this.showName = showName;
        this.contents = contents;
        this.images = images;
        this.showDate = showDate;
    }

    public void update(String title, String artistName, String showName, String contents, List<String> images, OffsetDateTime showDate) {
        if(title != null) this.title = title;
        if(artistName != null) this.artistName = artistName;
        if(showName != null) this.showName = showName;
        if(contents != null) this.contents = contents;
        if(images != null) this.images = images;
        if(showDate != null) this.showDate = showDate;
    }
}

package hello.atfeelogbackend.domain.filemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FileManager {

    // type FileManager {
    //_id: ID!
    //url: String!
    //size: Float
    //isUsed: Boolean!
    //createdAt: DateTime!
    //updatedAt: DateTime!
    //deletedAt: DateTime
    //}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private Double size;

    private boolean isUsed;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;

    public FileManager(String url, Double size, boolean isUsed) {
        this.url = url;
        this.size = size;
        this.isUsed = isUsed;
        this.deletedAt = null;
    }

}

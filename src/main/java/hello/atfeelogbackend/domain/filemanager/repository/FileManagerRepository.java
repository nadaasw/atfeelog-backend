package hello.atfeelogbackend.domain.filemanager.repository;

import hello.atfeelogbackend.domain.filemanager.entity.FileManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, Long> {
}

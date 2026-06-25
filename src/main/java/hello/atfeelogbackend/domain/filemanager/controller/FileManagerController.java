package hello.atfeelogbackend.domain.filemanager.controller;

import hello.atfeelogbackend.domain.filemanager.service.FileManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FileManagerController {

    private final FileManagerService fileManagerService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileManagerService.upload(file);
        log.info("Upload url: {}", url);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

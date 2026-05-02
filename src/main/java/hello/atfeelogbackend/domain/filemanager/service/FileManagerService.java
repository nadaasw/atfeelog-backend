package hello.atfeelogbackend.domain.filemanager.service;

import hello.atfeelogbackend.domain.filemanager.entity.FileManager;
import hello.atfeelogbackend.domain.filemanager.repository.FileManagerRepository;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileManagerService {

    private final FileManagerRepository fileManagerRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );



            return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
        }catch (Exception e){
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

    }



}

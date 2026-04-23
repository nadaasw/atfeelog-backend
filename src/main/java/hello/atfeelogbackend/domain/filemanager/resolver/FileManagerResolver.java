package hello.atfeelogbackend.domain.filemanager.resolver;

import hello.atfeelogbackend.domain.filemanager.entity.FileManager;
import hello.atfeelogbackend.domain.filemanager.service.FileManagerService;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FileManagerResolver {

    private final FileManagerService fileManagerService;

    @MutationMapping
    public FileManager uploadFile(@Argument MultipartFile file){
        try{
            String url = fileManagerService.upload(file);

            return FileManager.builder()
                    .url(url)
                    .size((double) file.getSize())
                    .isUsed(false)
                    .build();
        }catch (Exception e){
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

    }
}

package hello.atfeelogbackend.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    SUCCESS(HttpStatus.OK, "SUCCESS", "요청이 성공했습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "생성이 완료되었습니다."),
    UPDATED(HttpStatus.OK, "UPDATED", "수정이 완료되었습니다."),
    DELETED(HttpStatus.OK, "DELETED", "삭제가 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

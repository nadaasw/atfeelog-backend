package hello.atfeelogbackend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Not Found"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access Denied"),


    // User
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid Input"),
    LOGOUT_ERROR(HttpStatus.UNAUTHORIZED, "Logout Error"),
    USER_UPDATE_ERROR(HttpStatus.BAD_REQUEST, "User Update Error"),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Token Not Found"),
    TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED, "Token Not Match"),

    // File
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

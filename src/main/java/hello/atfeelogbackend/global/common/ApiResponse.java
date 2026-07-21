package hello.atfeelogbackend.global.common;

import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private List<FieldError> errors;

    public ApiResponse(String code, String message, T data, List<FieldError> errors){
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    // Success

    // 데이터 있을 경우
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data){
        return new ApiResponse<T>(
                successCode.getCode(),
                successCode.getMessage(),
                data,
                null
        );
    }

    // 데이터 없을 경우
    public static ApiResponse<Void> success(SuccessCode successCode){
        return new ApiResponse<>(
                successCode.getCode(),
                successCode.getMessage(),
                null,
                null
        );
    }

    // ============================
    // Fail
    // ============================

    public static ApiResponse<Void> fail(ErrorCode errorCode, List<FieldError> errors){
        return new ApiResponse<>(
                "fail",
                errorCode.getMessage(),
                null,
                errors
        );
    }

    public static ApiResponse<Void> fail(ErrorCode errorCode){
        return new ApiResponse<>(
                "fail",
                errorCode.getMessage(),
                null,
                null
        );
    }


}

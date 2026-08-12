package hello.atfeelogbackend.global.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalException {


    @GraphQlExceptionHandler(CustomException.class)
    public GraphQLError handleCustomException(CustomException e, DataFetchingEnvironment env) {
        log.warn("CustomException: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message(e.getMessage())
                .extensions(
                        java.util.Map.of(
                                "status", e.getErrorCode().getHttpStatus().value()
                        )
                )
                .build();
    }


    @GraphQlExceptionHandler(ConstraintViolationException.class)
    public GraphQLError handleValidationException(
            ConstraintViolationException e,
            GraphqlErrorBuilder<?> errorBuilder
    ) {
        List<Map<String, String>> validationErrors =
                e.getConstraintViolations()
                        .stream()
                        .map(violation -> Map.of(
                                "field", extractFieldName(
                                        violation.getPropertyPath().toString()
                                ),
                                "message", violation.getMessage()
                        ))
                        .toList();

        log.warn("GraphQL validation failed: {}", validationErrors);

        return errorBuilder
                .errorType(ErrorType.BAD_REQUEST)
                .message("입력값 검증에 실패했습니다.")
                .extensions(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "errors", validationErrors
                ))
                .build();
    }

    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return lastDotIndex >= 0
                ? propertyPath.substring(lastDotIndex + 1)
                : propertyPath;
    }

    // @PreAuthorize 등 Spring Security 메서드 보안 실패 - 권한 문제일 뿐이므로 500이 아닌 403으로 응답
    @GraphQlExceptionHandler(AccessDeniedException.class)
    public GraphQLError handleAccessDeniedException(AccessDeniedException e, DataFetchingEnvironment env) {
        log.warn("AccessDeniedException: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.FORBIDDEN)
                .message(ErrorCode.ACCESS_DENIED.getMessage())
                .extensions(Map.of("status", ErrorCode.ACCESS_DENIED.getHttpStatus().value()))
                .build();
    }

    // DB 제약(길이 초과, NOT NULL 등) 위반 - 요청 데이터 문제이므로 500이 아닌 400으로 응답
    @GraphQlExceptionHandler(DataIntegrityViolationException.class)
    public GraphQLError handleDataIntegrityViolationException(DataIntegrityViolationException e, DataFetchingEnvironment env) {
        log.warn("DataIntegrityViolationException: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("입력값이 올바르지 않습니다. 글자 수 제한 등을 확인해주세요.")
                .extensions(Map.of("status", HttpStatus.BAD_REQUEST.value()))
                .build();
    }

    // CustomException으로 아직 정리되지 않은 잘못된 입력/상태에 대한 방어용 - 500이 아닌 400으로 응답
    @GraphQlExceptionHandler(IllegalArgumentException.class)
    public GraphQLError handleIllegalArgumentException(IllegalArgumentException e, DataFetchingEnvironment env) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(e.getMessage())
                .extensions(Map.of("status", HttpStatus.BAD_REQUEST.value()))
                .build();
    }

    // 위에서 잡지 못한, 정말 예상 못한 에러만 여기로 떨어짐 - 원인 추적을 위해 스택트레이스까지 남김
    @GraphQlExceptionHandler(Exception.class)
    public GraphQLError handleException(Exception e, DataFetchingEnvironment env) {
        log.error("Unhandled exception in {}", env.getExecutionStepInfo().getPath(), e);

        return GraphqlErrorBuilder.newError(env)
                .message("Internal Error")
                .build();
    }
}

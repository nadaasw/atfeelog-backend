package hello.atfeelogbackend.global.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
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

    @GraphQlExceptionHandler(Exception.class)
    public GraphQLError handleException(Exception e, DataFetchingEnvironment env) {
        log.warn("Exception: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message("Internal Error")
                .build();
    }
}

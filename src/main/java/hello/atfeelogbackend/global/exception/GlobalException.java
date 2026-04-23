package hello.atfeelogbackend.global.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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


    @GraphQlExceptionHandler(Exception.class)
    public GraphQLError handleException(Exception e, DataFetchingEnvironment env) {
        log.warn("Exception: {}", e.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message("Internal Error")
                .build();
    }
}

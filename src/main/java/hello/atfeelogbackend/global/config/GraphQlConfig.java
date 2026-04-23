package hello.atfeelogbackend.global.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType uploadScalar = GraphQLScalarType.newScalar()
                .name("Upload")
                .description("A file upload scalar")
                .coercing(new graphql.schema.Coercing<Object, Object>() {
                    @Override
                    public Object serialize(Object dataFetcherResult) { return dataFetcherResult; }
                    @Override
                    public Object parseValue(Object input) { return input; }
                    @Override
                    public Object parseLiteral(Object input) { return input; }
                })
                .build();

        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.DateTime)
                .scalar(uploadScalar);
    }
}

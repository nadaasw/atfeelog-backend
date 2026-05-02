package hello.atfeelogbackend.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* hello.atfeelogbackend.domain.*.resolver.*.*(..))")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        log.debug("=== {} 시작 ===", methodName);

        long start = System.currentTimeMillis();
        Object result = jp.proceed();
        long end = System.currentTimeMillis();

        log.debug("=== {} 완료 ({}ms) ===",methodName, (end - start));
        return result;
    }
}

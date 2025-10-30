package com.example.hello.config;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestResponseLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingAspect.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Around("within(com.example.hello.controller..*)")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        log.info(
                "Incoming request - method: {}, timestamp: {}, args: {}",
                joinPoint.getSignature(),
                FORMATTER.format(start.atZone(ZoneId.systemDefault())),
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            Instant end = Instant.now();
            log.info(
                    "Outgoing response - method: {}, timestamp: {}, durationMs: {}, response: {}",
                    joinPoint.getSignature(),
                    FORMATTER.format(end.atZone(ZoneId.systemDefault())),
                    Duration.between(start, end).toMillis(),
                    result);
            return result;
        } catch (Throwable ex) {
            Instant errorTime = Instant.now();
            log.error(
                    "Exception in method: {}, timestamp: {}, durationMs: {}",
                    joinPoint.getSignature(),
                    FORMATTER.format(errorTime.atZone(ZoneId.systemDefault())),
                    Duration.between(start, errorTime).toMillis(),
                    ex);
            throw ex;
        }
    }
}

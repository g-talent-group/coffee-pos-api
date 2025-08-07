package com.coffee.pos.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    // 定義切點
    @Pointcut("execution(* com.coffee.pos.service.*.*(..))")
    public void serviceLayer() {}

    // Before建議
    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Method {} is about to execute with arguments: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }
}
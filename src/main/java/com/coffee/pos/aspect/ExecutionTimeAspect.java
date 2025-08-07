package com.coffee.pos.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        try {
            // 執行目標方法
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().getName(),
                    executionTime);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {} failed after {} ms with error: {}",
                    joinPoint.getSignature().getName(),
                    executionTime,
                    e.getMessage());
            throw e;
        }
    }
}
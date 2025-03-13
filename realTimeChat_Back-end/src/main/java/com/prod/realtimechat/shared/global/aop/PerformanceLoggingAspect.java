package com.prod.realtimechat.shared.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 성능 측정을 위한 AOP 클래스.
 */
@Slf4j  // 로그 출력을 위한 Lombok 어노테이션
@Aspect  // AOP 기능을 수행하는 클래스임을 선언
@Component  // Spring의 Bean으로 등록하여 자동으로 관리되도록 함
public class PerformanceLoggingAspect {

    /**
     * @Around: 특정 메서드 실행 전후로 AOP 로직을 수행하는 어노테이션.
     * execution(* com.yourproject.chat..*(..)):
     *  - com.yourproject.chat 패키지 및 하위 패키지 내의 모든 클래스의 모든 메서드 실행을 감지.
     *  - '..'은 하위 패키지를 포함한다는 의미.
     *  - '(..)'은 모든 매개변수 유형을 포함한다는 의미.
     */
    @Around("execution(* com.prod.realtimechat.chat.presentation..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis(); // 실행 시작 시간 기록

        Object result = joinPoint.proceed();  // 실제 대상 메서드 실행

        long elapsedTime = System.currentTimeMillis() - start; // 실행 종료 후 경과 시간 계산
        log.info("[PERFORMANCE] {} executed in {} ms", joinPoint.getSignature(), elapsedTime);

        return result; // 원래 메서드의 실행 결과 반환
    }
}

package com.curr_convert.currency_converter.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Logger {
    org.slf4j.Logger logger= LoggerFactory.getLogger(Logger.class);
    @Before("execution(* com.curr_convert.currency_converter..*.*(..))")
    public void printLogsBefore(JoinPoint joinPoint){
        logger.info("Calling method {}", joinPoint.getSignature().getName());
    }

    @After("execution(* com.curr_convert.currency_converter..*.*(..))")
    public void printLogsAfter(JoinPoint joinPoint){
        logger.info("Mathod "+ joinPoint.getSignature().getName()+ " Executed!");
    }
}

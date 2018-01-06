package org.ums.servicea;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Aspect
public class ServiceAAspect {
    private static Logger logger = LoggerFactory.getLogger(ServiceAAspect.class);
    @After("execution(* org.ums.servicea.*..*(..))")
    public void after(JoinPoint joinPoint)throws  Throwable{
        JoinPoint point=joinPoint;
        logger.info(joinPoint.getTarget().toString());
    }
}

package io.choerodon.test.manager.infra.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/28/18.
 */
@Aspect
@Component
public class FixDataAspect {
    private Long startTime;
    @Pointcut("execution(* io.choerodon.test.manager.app.service.impl.DataMigrationServiceImpl.fixData(..))")
    public void aspect(){

    }
    @Before("aspect()")
    public void before(JoinPoint joinPoint){
        System.out.println("=======================================");
        startTime = System.currentTimeMillis();
        System.out.println(startTime);

    }
    @After("aspect()")
    public void after(JoinPoint joinPoint){
        System.out.println("========================================");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);
    }

}

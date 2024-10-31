package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {


    @Pointcut(value = "@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    /**
     *
     * @param joinPoint  内部封装了切入点的信息
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        System.out.println("---------aop增强功能---------");

        //获取实体
        Object entity = joinPoint.getArgs()[0];
        //判断被切入的方法是新增还是修改
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取切入点的方法
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        try {
            if (operationType==OperationType.INSERT){
                //插入
                //给实体的属性赋值
                //反射机制获取方法
                Class<?> clazz = entity.getClass();
                Method setCreateUser = clazz.getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = clazz.getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setCreateTime = clazz.getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = clazz.getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //调用方法
                setCreateUser.invoke(entity, BaseContext.getCurrentId());
                setUpdateUser.invoke(entity,BaseContext.getCurrentId());
                setCreateTime.invoke(entity,LocalDateTime.now());
                setUpdateTime.invoke(entity,LocalDateTime.now());
            }else if(operationType==OperationType.UPDATE){
                //修改
                //给实体的属性赋值
                Class<?> clazz = entity.getClass();
                Method setUpdateUser = clazz.getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setUpdateTime = clazz.getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //调用方法
                setUpdateUser.invoke(entity,BaseContext.getCurrentId());
                setUpdateTime.invoke(entity,LocalDateTime.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

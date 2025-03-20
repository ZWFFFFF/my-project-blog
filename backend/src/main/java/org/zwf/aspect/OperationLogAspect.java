package org.zwf.aspect;

import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.zwf.annotation.OperationLog;
import org.zwf.entity.dto.OperationLogEntity;
import org.zwf.service.OperationLogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * 日志记录拦截AOP
 */
@Aspect // 表示这是一个AOP切面类
@Component
public class OperationLogAspect {
    @Resource
    private OperationLogService operationLogService;

    // 拦截带有@OperationLog注解的方法
    @Around("@annotation(org.zwf.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 通过反射获取对应注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog annotation = signature.getMethod().getAnnotation(OperationLog.class);

        // 获取操作类型
        String operationType = annotation.operationType();
        // 获取操作人Id
        Integer operatorId = getCurrentUserId();
        // 获取操作详情（方法参数）
        String operationDetail = Arrays.toString(joinPoint.getArgs());

        // 记录操作日志
        OperationLogEntity log = new OperationLogEntity();
        log.setOperationTime(new Date());
        log.setOperatorId(operatorId);
        log.setOperationType(operationType);
        log.setOperationDetail(operationDetail);

        // 执行目标方法
        try {
            Object result = joinPoint.proceed(); // 执行目标方法
            log.setOperationResult("SUCCESS"); // 目标方法执行成功
            return result;
        } catch (Exception e) {
            log.setOperationResult("FAILED"); // 目标方法执行失败
            throw e; // 继续抛出异常
        } finally {
            operationLogService.saveLog(log); // 保存操作日志
        }
    }

    private Integer getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Integer.valueOf(user.getUsername());
    }
}

package org.zwf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录操作日志注解
 */
@Target(ElementType.METHOD) // 表示该注解只能用于方法上
@Retention(RetentionPolicy.RUNTIME) // 表示这个注解在运行时可以通过反射获取
public @interface OperationLog {
    String operationType(); // 定义一个属性，指定操作类型
}

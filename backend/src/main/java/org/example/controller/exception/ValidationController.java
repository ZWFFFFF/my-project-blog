package org.example.controller.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.RestBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 用于接口参数校验处理的控制器
 */
@Slf4j
@RestControllerAdvice
public class ValidationController {
    /**
     * 校验不通过打印警告信息，而不是直接抛出异常
     * @param e 异常
     * @return 响应实体对象
     */
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    public RestBean<Void> validationExceptionHandler(Exception e) {
        log.warn("Resolve [{}: {}]", e.getClass().getName(), e.getMessage());
        return RestBean.argumentNotValid("请求参数有误");
    }
}

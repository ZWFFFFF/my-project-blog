package org.example.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.entity.RestBean;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 专用用于处理错误请求的Controller
 */
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class ErrorRequestController extends AbstractErrorController {

    public ErrorRequestController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    /**
     * 所有错误在这里统一处理，自动解析状态码和原因
     *
     * @param request 请求
     * @return 失败响应
     */
    @RequestMapping
    public RestBean<Void> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, this.getAttributeOptions());

        return this.convertErrorMessage(status)
                .orElse(RestBean.failure(status.value(), errorAttributes.get("message").toString()));
    }

    /**
     * 对于一些特殊的状态码，错误信息转换
     * @param status 状态码
     * @return 错误信息
     */
    private Optional<RestBean<Void>> convertErrorMessage(HttpStatus status){
        RestBean<Void> value = switch (status.value()) {
            case 400 -> RestBean.argumentNotValid("请求参数有误");
            case 404 -> RestBean.notFound("请求的接口不存在");
            case 405 -> RestBean.methodNotAllowed("请求方法错误");
            case 500 -> RestBean.internalServerError("内部错误，请联系管理员");
            default -> null;
        };
        return Optional.ofNullable(value);
    }

    /**
     * 错误属性获取选项，这里额外添加了错误消息和异常类型
     * @return 选项
     */
    private ErrorAttributeOptions getAttributeOptions(){
        return ErrorAttributeOptions
                .defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.EXCEPTION);
    }
}
package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * 用于验证相关Controller包含用户的注册、重置密码等操作
 */
@Validated // 开启方法参数校验
@RestController
@RequestMapping("/api/auth")
@Tag(name = "验证相关接口", description = "包括用户登录、注册、验证码请求等操作")
public class AuthorizeController {
    @Resource
    private AccountService accountService;

    /**
     * 请求邮件验证码
     * @param email 邮箱
     * @param type 请求邮件类型
     * @param request 请求
     * @return 响应实体对象
     */
    @GetMapping("/ask-code")
    @Operation(summary = "请求邮件验证码")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset|login)") String type,
                                        HttpServletRequest request) {
        return messageHandler(() -> accountService.emailVerifyCode(type, email, request.getRemoteAddr()));
    }


    @PostMapping("/verifyCode-login")
    @Operation(summary = "验证码登录")
    public RestBean<AuthorizeVO> verifyCodeLogin(@RequestBody @Valid VerifyCodeLoginVO vo) {
        return accountService.loginByVerifyCode(vo);
    }

    /**
     * 对于业务返回信息进行封装响应实体
     * @param action 执行业务操作
     * @return 响应实体
     */
    private RestBean<Void> messageHandler(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.argumentNotValid(message);
    }
}

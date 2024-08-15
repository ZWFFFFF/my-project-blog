package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.ResetPasswordVO;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return RestBean.messageHandler(() -> accountService.emailVerifyCode(type, email, request.getRemoteAddr()));
    }


    /**
     * 邮箱验证码登录
     * @param vo 验证码登录表单封装
     * @return 响应实体对象
     */
    @PostMapping("/code-login")
    @Operation(summary = "验证码登录")
    public RestBean<AuthorizeVO> verifyCodeLogin(@RequestBody @Valid VerifyCodeLoginVO vo) {
        return accountService.loginByVerifyCode(vo);
    }

    /**
     * 用户注册
     * @param vo 注册表单封装
     * @return 响应实体对象
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return RestBean.messageHandler(() -> accountService.registerAccount(vo));
    }

    /**
     * 重置密码（登录前）
     * @param vo 重置密码表单封装
     * @return 响应实体对象
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码")
    public RestBean<Void> resetPassword(@RequestBody @Valid ResetPasswordVO vo) {
        return RestBean.messageHandler(() -> accountService.resetPassword(vo));
    }
}

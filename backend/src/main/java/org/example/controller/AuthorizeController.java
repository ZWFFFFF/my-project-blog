package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
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
public class AuthorizeController {
    @Resource
    private AccountService accountService;

//    /**
//     * 请求邮件验证码
//     * @param email 邮箱
//     * @param type 请求邮件类型
//     * @param request 请求
//     * @return 响应实体对象
//     */
//    @GetMapping("/ask-code")
//    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
//                                        @RequestParam @Pattern(regexp = "(register|reset|login)") String type,
//                                        HttpServletRequest request) {
//
//    }

    @PostMapping("/verifyCode-login")
    public void verifyCodeLogin() {
    }

//    private RestBean<Void> messageHandler(Supplier<String> action) {
//        String message = action.get();
//        return message == null ? RestBean.success() : RestBean.failure()
//    }
}

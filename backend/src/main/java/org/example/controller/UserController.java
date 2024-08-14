package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.response.AccountVO;
import org.example.service.AccountService;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关Controller，包含用户信息查询、修改等操作
 */
@Validated
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户相关接口", description = "包括用户信息修改、查询等操作")
public class UserController {
    @Resource
    private AccountService accountService;

    /**
     * 修改用户名
     * @param newUsername 新的用户名
     * @return 修改结果
     */
    @GetMapping("/change-username")
    @Operation(summary = "修改用户名")
    public RestBean<Void> changeUsername(@RequestParam
                                         @Length(min = 1, max = 10)
                                         @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
                                         String newUsername) {
        return RestBean.messageHandler(() -> accountService.changeUsername(newUsername));
    }

    /**
     * 获取用户信息
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息")
    public RestBean<AccountVO> getUserInfo(@RequestParam @NotNull Integer id) {
        return accountService.getAccountInfoById(id);
    }

    // 获取用户列表、注销用户
}

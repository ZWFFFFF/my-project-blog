package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.ChangePasswordVO;
import org.example.entity.vo.response.AccountVO;
import org.example.service.AccountService;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @return 响应实体
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
     * @return 响应实体
     */
    @GetMapping("/user-info")
    @Operation(summary = "获取用户信息")
    public RestBean<AccountVO> getUserInfo(@RequestParam @NotNull Integer id) {
        return accountService.getAccountInfoById(id);
    }

    /**
     * 获取用户列表
     * @return 响应实体
     */
    @GetMapping("/user-list")
    @Operation(summary = "获取用户列表")
    public RestBean<List<AccountVO>> getUserList()  {
        return accountService.getAllUser();
    }

    /**
     * 注销账号
     * @param id 用户id
     * @param username 用户名
     * @return 响应实体
     */
    @GetMapping("/delete-account")
    @Operation(summary = "注销账号")
    public RestBean<Void> deleteAccount(@RequestParam @NotNull Integer id,
                                        @RequestParam @Length(min = 1, max = 20) String username)  {
        return null;
    }

    /**
     * 修改密码
     * @param vo 修改密码表单封装
     * @return 响应实体
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo) {
        return null;
    }
}

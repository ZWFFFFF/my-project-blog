package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.example.entity.RestBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员相关Controller，包含封禁用户、解锁用户、审核投稿等功能
 */
@Validated
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员相关接口", description = "包含封禁用户、解锁用户、审核投稿等功能")
public class AdminController {

    /**
     * 封禁用户
     * @param userId 用户id
     * @return 响应实体
     */
    @GetMapping("/ban-user")
    @Operation(summary = "封禁用户")
    public RestBean<Void> banUser(@RequestParam @NotNull Integer userId) {
        System.out.println("hello world");
        return null;
    }
}

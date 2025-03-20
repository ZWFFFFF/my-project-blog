package org.zwf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.zwf.annotation.OperationLog;
import org.zwf.entity.RestBean;
import org.zwf.entity.vo.response.OperationLogVO;
import org.zwf.service.AccountService;
import org.zwf.service.OperationLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员相关Controller，包含封禁用户、审核投稿等功能
 */
@Validated
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员相关接口", description = "包含封禁用户、解锁用户、审核投稿等功能")
public class AdminController {
    @Resource
    private AccountService accountService;
    @Resource
    private OperationLogService operationLogService;

    /**
     * 封禁用户
     * @param userId 用户id
     * @return 响应实体
     */
    @OperationLog(operationType = "BAN_USER")
    @GetMapping("/ban-user")
    @Operation(summary = "封禁用户")
    public RestBean<Void> banUser(@RequestParam @NotNull Integer userId) {
        return RestBean.messageHandler(() -> accountService.banAccount(userId));
    }

    /**
     * 解封用户
     * @param userId 用户id
     * @return 响应实体
     */
    @OperationLog(operationType = "UNBAN_USER")
    @GetMapping("/unban-user")
    @Operation(summary = "解封用户")
    public RestBean<Void> unbanUser(@RequestParam @NotNull Integer userId) {
        return RestBean.messageHandler(() -> accountService.unbanAccount(userId));
    }

    /**
     * 获取操作日志
     * @return 响应实体
     */
    @GetMapping("/get-operation-log")
    @Operation(summary = "获取操作日志")
    public RestBean<List<OperationLogVO>> getOperationLog() {
        return operationLogService.getLogs();
    }
}

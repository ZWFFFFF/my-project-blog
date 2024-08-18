package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登录成功用户授权数据信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeVO {
    private Integer id;
    private String username;
    private String role;
    private String token; // JWT令牌
    private Date expire; // 用户登录吊销时间
}

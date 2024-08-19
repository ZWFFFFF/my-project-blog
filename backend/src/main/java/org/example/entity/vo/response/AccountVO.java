package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息实体封装
 */
@Data
public class AccountVO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private Date registerTime;
}

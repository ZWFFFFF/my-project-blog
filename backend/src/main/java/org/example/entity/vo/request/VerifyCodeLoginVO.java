package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 验证码登录表单实体
 */
@Data
public class VerifyCodeLoginVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
}

package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 验证码登录表单实体
 */
@Data
public class VerifyCodeLoginVO {
    @Email
    @NotBlank
    private String email;
    @Length(max = 6)
    private String code;
}

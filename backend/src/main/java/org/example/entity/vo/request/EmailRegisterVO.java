package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 注册表单实体
 */
@Data
public class EmailRegisterVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
    @Length(min = 6, max = 20)
    private String password;
}

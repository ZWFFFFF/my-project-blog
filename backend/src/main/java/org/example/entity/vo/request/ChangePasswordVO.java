package org.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 修改密码表单实体
 */
@Data
public class ChangePasswordVO {
    @NotNull
    private Integer id;
    @Length(min = 6, max = 20)
    private String oldPassword;
    @Length(min = 6, max = 20)
    private String newPassword;
}

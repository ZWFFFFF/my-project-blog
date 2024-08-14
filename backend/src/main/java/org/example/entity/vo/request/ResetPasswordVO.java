package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
    @Length(min = 6, max = 20)
    private String password;
}

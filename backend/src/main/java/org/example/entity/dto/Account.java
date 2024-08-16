package org.example.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class Account {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
    private Date registerTime;
    private int active; // 0:不可使用 1:可使用

    public Account(String password, String email) {
        this.password = password;
        this.email = email;
    }
}

package org.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private Date registerTime;
}

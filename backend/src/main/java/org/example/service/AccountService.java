package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    String emailVerifyCode(String type, String email, String ip);

    RestBean<AuthorizeVO> loginByVerifyCode(VerifyCodeLoginVO vo);

}
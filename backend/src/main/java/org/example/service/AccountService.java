package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.ChangePasswordVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.ResetPasswordVO;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AccountVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends UserDetailsService {
    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    String emailVerifyCode(String type, String email, String ip);
    RestBean<AuthorizeVO> loginByVerifyCode(VerifyCodeLoginVO vo);
    String registerAccount(EmailRegisterVO vo);
    String resetPassword(ResetPasswordVO vo);
    String changeUsername(Integer id, String newUsername);
    RestBean<AccountVO> getAccountInfoById(Integer id);
    RestBean<List<AccountVO>> getAllUser();
    boolean isCurrentUser(Integer userId);
    String deleteAccount(Integer userId, String username, String token);
    String changePassword(ChangePasswordVO vo);
    String banAccount(Integer userId);
    String unbanAccount(Integer userId);
    boolean isAccountBanned(Integer userId);
    boolean isAccountExistById(Integer userId);
    String getUsernameById(Integer userId);
}
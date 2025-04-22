package org.zwf.service;

import org.springframework.web.multipart.MultipartFile;
import org.zwf.entity.RestBean;
import org.zwf.entity.dto.Account;
import org.zwf.entity.vo.request.ChangePasswordVO;
import org.zwf.entity.vo.request.EmailRegisterVO;
import org.zwf.entity.vo.request.ResetPasswordVO;
import org.zwf.entity.vo.request.VerifyCodeLoginVO;
import org.zwf.entity.vo.response.AccountVO;
import org.zwf.entity.vo.response.AuthorizeVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface AccountService extends UserDetailsService {
    Account findAccountByUsername(String username);
    String emailVerifyCode(String type, String email, String ip);
    RestBean<AuthorizeVO> loginByVerifyCode(VerifyCodeLoginVO vo);
    String registerAccount(EmailRegisterVO vo);
    String resetPassword(ResetPasswordVO vo);
    String changeUsername(Integer id, String newUsername);
    String changeEmail(Integer id, String newEmail);
    RestBean<AccountVO> getAccountInfoById(Integer id);
    RestBean<List<AccountVO>> getAllUser();
    boolean isCurrentUser(Integer userId);
    String deleteAccount(Integer userId, String token);
    String changePassword(ChangePasswordVO vo);
    String banAccount(Integer userId);
    String unbanAccount(Integer userId);
    boolean isAccountBanned(Integer userId);
    boolean isAccountExistById(Integer userId);
    String getUsernameById(Integer userId);
    String getUserAvatarById(Integer userId);
    RestBean<Map<String, String>> saveAvatar(MultipartFile file);
}
package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.dto.Account;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户信息处理相关服务
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;

    /**
     * 从数据库中通过邮箱查找用户详细信息（用户登录表单信息校验）
     * @param username 邮箱
     * @return 用户详细信息
     * @throws UsernameNotFoundException 用户名或密码未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountMapper.getAccountByEmail(username);
        if(account == null) {
            throw new UsernameNotFoundException("邮箱或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 从数据库中通过用户名查找用户
     * @param username 用户名
     * @return 用户实例
     */
    @Override
    public Account findAccountByUsername(String username) {
        return accountMapper.getAccountByName(username);
    }
}

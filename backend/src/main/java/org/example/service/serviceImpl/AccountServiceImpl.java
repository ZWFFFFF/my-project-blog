package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.ChangePasswordVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.ResetPasswordVO;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AccountVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtil;
import org.example.utils.JwtUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息处理相关服务
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private FlowUtil flowUtil;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PasswordEncoder encoder;

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
            throw new UsernameNotFoundException("邮箱或密码错误"); // 邮箱填错时
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

    /**
     * 从数据库中通过邮箱查找用户
     * @param email 邮箱
     * @return 用户实例
     */
    @Override
    public Account findAccountByEmail(String email) {
        return accountMapper.getAccountByEmail(email);
    }

    /**
     * 生成注册验证码存入Redis中限时5分钟，并将邮件发送请求提交到消息队列等待监听器获取并发送邮件
     * @param type 邮件类型
     * @param email 邮箱
     * @param ip ip地址
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String emailVerifyCode(String type, String email, String ip) {
        // ip.intern():如果池中已经有了一个等于此字符串的字符串，则返回这个字符串的引用；否则，将此字符串加入池中，并返回这个字符串的引用。
        // 所有的String ip都是同一对象，所有线程共享这个对象，
        // synchronized(线程竞争的资源):确保在同一时间只有一个线程能够执行某个代码块，从而避免线程安全问题
        synchronized (ip.intern()) {
            if(!verifyEmailCodeLimit(ip)) return "请求频繁，请稍后再试"; // 获取验证码限流
            // 生成验证码
            Random random = new Random();
            int code = random.nextInt(900000) + 100000;
            // 验证码放入消息队列
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            // 保存验证码入redis
            stringRedisTemplate.opsForValue().set(this.getCodeKey(email), String.valueOf(code), 5, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 根据验证码登录
     * @param vo 验证码登录表单
     * @return 响应实体
     */
    @Override
    public RestBean<AuthorizeVO> loginByVerifyCode(VerifyCodeLoginVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(this.getCodeKey(email));

        if(!this.isAccountExistByEmail(email)) return RestBean.unauthorized("邮箱不存在，请先注册");
        if(code == null) return RestBean.unauthorized("请先获取验证码");
        if(!code.equals(vo.getCode())) return RestBean.unauthorized("验证码输入错误，请重新输入");

        // 生成jwt令牌
        Account account = accountMapper.getAccountByEmail(email);
        String token = jwtUtil.createJwt(account.getId(), account.getRole());

        // 封装用户权限信息实体
        AuthorizeVO authorizeVO = new AuthorizeVO(account.getId(), account.getUsername(), account.getRole(), token, jwtUtil.expireTime());

        stringRedisTemplate.delete(this.getCodeKey(email)); // 删除验证码
        return RestBean.success(authorizeVO);
    }

    /**
     * 注册账号
     * @param vo 注册表单信息封装
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String registerAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(this.getCodeKey(email));

        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误，请重新输入";
        if(this.isAccountExistByEmail(email)) return "该邮箱已被注册";

        String password = encoder.encode(vo.getPassword());
        Account account = new Account(password, email);

        if(this.addAccount(account)) {
            stringRedisTemplate.delete(this.getCodeKey(email)); // 删除验证码
            return null;
        } else {
            return "发生了一些错误，请联系管理员";
        }
    }

    /**
     * 登录前忘记密码进行重置
     * @param vo 重置密码表单信息封装
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String resetPassword(ResetPasswordVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(this.getCodeKey(email));

        if(!isAccountExistByEmail(email)) return "该邮箱不存在";
        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误，请重新输入";

        String password = encoder.encode(vo.getPassword());

        Integer update = accountMapper.updateAccountPasswordByEmail(email, password);
        if(update == 1) {
            stringRedisTemplate.delete(this.getCodeKey(email)); // 删除验证码
            return null;
        } else {
            return "发生了一些错误，请联系管理员";
        }
    }

    /**
     * 修改用户名
     * @param id 用户id
     * @param newUsername 新的用户名
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String changeUsername(Integer id, String newUsername) {
        if(!this.isCurrentUser(id)) return "非法操作";

        if(isAccountExistByName(newUsername)) return "该用户名已存在";

        Account account = accountMapper.getAccountById(id);
        if(account == null) return "发生了一些错误，请联系管理员";

        accountMapper.updateUsername(newUsername, account.getUsername());
        return null;
    }

    /**
     * 通过用户id获取用户信息
     * @param id 用户id
     * @return 响应实体
     */
    @Override
    public RestBean<AccountVO> getAccountInfoById(Integer id) {
        Account account = accountMapper.getAccountById(id);
        if(account == null) return RestBean.argumentNotValid("该用户不存在");
        AccountVO vo = this.toAccountVO(account);
        return RestBean.success(vo);
    }

    /**
     * 获取所有User用户信息
     * @return 响应实体
     */
    @Override
    public RestBean<List<AccountVO>> getAllUser() {
        List<Account> accountList = accountMapper.getAccountByRole("USER");
        if(accountList == null) return RestBean.internalServerError("发生了一些错误，请联系管理员");

        List<AccountVO> voList = new ArrayList<>();
        for(Account account: accountList) {
            voList.add(this.toAccountVO(account));
        }
        return RestBean.success(voList);
    }

    /**
     * 判断请求者是否为当前用户
     * @param userId 用户id
     * @return true or false
     */
    @Override
    public boolean isCurrentUser(Integer userId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername().equals(String.valueOf(userId));
    }

    /**
     * 删除账号
     * @param userId 用户id
     * @param username 用户名
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String deleteAccount(Integer userId, String username, String token) {
        if(!this.isCurrentUser(userId)) return "非法操作";
        Account account = accountMapper.getAccountById(userId);
        if(account == null) return "用户不存在";
        if(!account.getUsername().equals(username)) return "用户名不匹配";

        int delete = accountMapper.deleteAccountById(userId);
        if(delete <= 0) return "发生了一些错误，请联系管理员";
        // 吊销当前用户jwt
        if(jwtUtil.revokeJwt(token)) {
            return null;
        } else {
            return "发生了一些错误，请联系管理员";
        }
    }

    /**
     * 修改密码
     * @param vo 修改密码表单实体
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String changePassword(ChangePasswordVO vo) {
        Integer userId = vo.getId();
        String oldPassword = vo.getOldPassword();

        if(!this.isCurrentUser(userId)) return "非法操作";
        Account account = accountMapper.getAccountById(userId);
        if(account == null) return "用户不存在";
        if(!encoder.matches(oldPassword, account.getPassword())) return "旧密码不正确";

        String newPassword = encoder.encode(vo.getNewPassword());
        int update = accountMapper.updateAccountPasswordByEmail(account.getEmail(), newPassword);
        if(update <= 0) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 封禁账号
     * @param userId 用户id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String banAccount(Integer userId) {
        int update = accountMapper.banAccountById(userId);
        if(update <= 0) return "用户不存在";
        return null;
    }

    /**
     * 解封账号
     * @param userId 用户id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String unbanAccount(Integer userId) {
        int update = accountMapper.unbanAccountById(userId);
        if(update <= 0) return "用户不存在";
        return null;
    }

    /**
     * 判断账号是否被封禁
     * @param userId 用户id
     * @return true表示被封禁，false表示未被封禁
     */
    @Override
    public boolean isAccountBanned(Integer userId) {
        Account account = accountMapper.getAccountById(userId);
        return account.getActive() != 1;
    }

    /**
     * 判断账号是否存在
     * @param userId 用户id
     * @return true表示存在，false表示不存在
     */
    @Override
    public boolean isAccountExistById(Integer userId) {
        return accountMapper.getAccountById(userId) != null;
    }

    /**
     * 获取用户名
     * @param userId 用户id
     * @return 用户名
     */
    @Override
    public String getUsernameById(Integer userId) {
        return accountMapper.getUsernameById(userId);
    }

    /**
     * 将用户实体转换为用户信息实体
     * @param account 用户实体
     * @return 用户信息实体
     */
    private AccountVO toAccountVO(Account account) {
        AccountVO vo = new AccountVO();
        vo.setId(account.getId());
        vo.setUsername(account.getUsername());
        vo.setEmail(account.getEmail());
        vo.setRole(account.getRole());
        vo.setRegisterTime(account.getRegisterTime());
        return vo;
    }

    /**
     * 针对IP地址进行邮件验证码获取限流(60s)
     * @param ip ip地址
     * @return true表示可以获取验证码并对ip限流，false表示不能获取ip正在受限制
     */
    private boolean verifyEmailCodeLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtil.limitOnceCheck(key, 60);
    }

    /**
     * 获取Redis中存储的邮件验证码的key
     * @param email 邮箱
     * @return 邮件验证码
     */
    private String getCodeKey(String email) {
        return Const.VERIFY_EMAIL_DATA + email;
    }

    /**
     * 根据邮箱判断账号是否存在
     * @param email 邮箱
     * @return true表示存在，false表示不存在
     */
    private boolean isAccountExistByEmail(String email) {
        return accountMapper.getAccountByEmail(email) != null;
    }

    /**
     * 根据用户名判断账号是否存在
     * @param name 用户名
     * @return true表示存在，false表示不存在
     */
    private boolean isAccountExistByName(String name) {
        return accountMapper.getAccountByName(name) != null;
    }

    /**
     * 添加账号
     * @param account 账号信息
     * @return true表示添加成功，false表示添加失败
     */
    private boolean addAccount(Account account) {
        return accountMapper.insertAccount(account) == 1;
    }
}

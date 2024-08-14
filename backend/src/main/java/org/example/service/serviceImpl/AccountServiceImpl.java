package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.VerifyCodeLoginVO;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtil;
import org.example.utils.JwtUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
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
        String token = jwtUtil.createJwt(account.getId(), account.getUsername(), account.getRole());

        // 封装用户权限信息实体
        AuthorizeVO authorizeVO = new AuthorizeVO(account.getUsername(), account.getRole(), token, jwtUtil.expireTime());

        return RestBean.success(authorizeVO);
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
}

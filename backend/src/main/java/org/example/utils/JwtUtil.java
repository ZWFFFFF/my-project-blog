package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// jwt包括加密算法，用户校验信息、令牌签名三部分，用于身份校验
// 前端登录成功后可以获取jwt，并保存到本地，下次请求需要携带上。后端获取jwt后需要解析jwt并获取用户信息和更新jwt
/**
 * 用于处理Jwt令牌的工具类
 */
@Component
public class JwtUtil {
    // 用于给Jwt令牌签名校验的秘钥
    @Value("${my-config.security.jwt.key}")
    private String key;
    // 令牌持续时间，单位为小时
    @Value("${my-config.security.jwt.expire}")
    private int expireTime;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 根据UserDetails生成Jwt令牌(存储用户权限信息)
     * @param id 用户唯一标识
     * @param authorities 用户权限
     * @return Jwt令牌
     */
    public String createJwt(int id, String authorities)  {
        Algorithm algorithm = Algorithm.HMAC256(key); // 设置加密算法
        Date expire = this.expireTime(); // jwt过期时间
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id) // 用户唯一标识
                .withClaim("authorities", authorities) // 用户权限
                .withExpiresAt(expire) // 设置过期时间
                .withIssuedAt(new Date()) // 令牌创建时间
                .sign(algorithm); // 使用算法进行签名
    }

    /**
     * 解析jwt令牌
     * @param headerToken 请求头部中的token
     * @return 解析后的jwt
     */
    public DecodedJWT resolveJwt(String headerToken) {
        String token = this.convertToken(headerToken); // 从请求头部中获取token
        if(token == null) return null;

        Algorithm algorithm = Algorithm.HMAC256(key); // 解析算法
        JWTVerifier jwtVerifier = JWT.require(algorithm).build(); // jwt校验器
        try {
            DecodedJWT verify = jwtVerifier.verify(token); // 校验jwt

            // jwt在黑名单中返回null
            if(this.isJwtInBlackList(verify.getId())) return null;

            // 如果令牌以过期就返回null，反之返回校验后的token
            return new Date().after(verify.getExpiresAt()) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 吊销令牌方法
     * @param headerToken 请求头部中的token
     * @return 是否成功
     */
    public boolean revokeJwt(String headerToken) {
        String token = this.convertToken(headerToken); // 从请求头部中获取token
        if(token == null) return false;

        Algorithm algorithm = Algorithm.HMAC256(key); // 解析算法
        JWTVerifier jwtVerifier = JWT.require(algorithm).build(); // jwt校验器

        try {
            DecodedJWT jwt = jwtVerifier.verify(token); // 校验jwt
            return this.putJwtInBlackList(jwt.getId(), jwt.getExpiresAt()); // 将jwtId放入黑名单
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 根据解析后的jwt生成UserDetails实体
     * @param jwt 解析后的jwt
     * @return UserDetails实体
     */
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("id").toString())
                .password("***********") // 密码不存
                .authorities(claims.get("authorities").asString())
                .build();
    }

    /**
     * 获取jwt过期时间
     * @return jwt过期时间
     */
    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expireTime);
        return calendar.getTime();
    }

    /**
     * 校验并转换请求头中的Token令牌
     * @param headerToken 请求头中的Token
     * @return 转换后的Token
     */
    private String convertToken(String headerToken) {
        if(headerToken == null || !headerToken.startsWith("Bearer ")) return null;
        return headerToken.substring(7);
    }

    /**
     * 判断jwt是否在黑名单中
     * @param jwtId jwt的唯一标识
     * @return 是否在黑名单中
     */
    private boolean isJwtInBlackList(String jwtId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + jwtId));
    }

    /**
     * 将令牌放入黑名单，存储在redis中
     * @param jwtId jwt的唯一标识
     * @param time jwt过期时间
     * @return 是否成功
     */
    private boolean putJwtInBlackList(String jwtId, Date time) {
        if(isJwtInBlackList(jwtId)) return false;

        long expire = Math.max(time.getTime() - new Date().getTime(), 0); // 黑名单中的jwt存放时间为：jwt过期时间 - 当前时间
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + jwtId, "", expire, TimeUnit.MILLISECONDS); // 存放如redis
        return true;
    }
}

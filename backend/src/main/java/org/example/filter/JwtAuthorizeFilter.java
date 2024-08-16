package org.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Jwt校验过滤器，需要加入到SpringSecurity的校验过滤链中
/*
    工作：每次请求都会获取请求头中jwt，如果没有jwt，继续SpringSecurity过滤链，返回权限校验失败响应
        如果有jwt就解析jwt生成UserDetail实体并直接让该用户通过SpringSecurity内部的身份验证
 */
/**
 * Jwt校验过滤器，取出请求头中的jwt并校验
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private AccountService accountService;

    // Jwt校验逻辑：
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中的Authorization字段读取值，格式为：Bearer Token
        String authorization = request.getHeader("Authorization");
        // 解析jwt
        DecodedJWT jwt = jwtUtil.resolveJwt(authorization);

        if(jwt != null) {
            UserDetails user = jwtUtil.toUser(jwt); // 将token中的信息解析成UserDetail对象
            Integer userId = Integer.valueOf(user.getUsername());

            // 判断用户是否存在(可能存在用户已经注销账号但自己保留了jwt的情况)和用户是否被封禁
            if (accountService.isAccountExistById(userId) && !accountService.isAccountBanned(userId))  {
                // 让用户通过校验
                UsernamePasswordAuthenticationToken authenticationToken = // 生成SpringSecurity内部的校验Token
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 直接让该用户通过SpringSecurity的身份校验
            }
        }

        filterChain.doFilter(request, response);
    }
}

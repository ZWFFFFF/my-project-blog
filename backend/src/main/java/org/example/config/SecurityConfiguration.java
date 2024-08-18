package org.example.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.filter.JwtAuthorizeFilter;
import org.example.service.AccountService;
import org.example.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

// SpringSecurity：一个权限验证框架，负责用户认证登录、用户授权、攻击防护等。
// 配置SpringSecurity
// SpringSecurity工作流程：
// 登录请求经过SpringSecurity过滤链 -> 根据表单的username执行loadUserByUsername方法从数据库查找用户信息返回一个UserDetails对象
// -> 表单的password与UserDatails的password进行比较 -> 认证成功，根据UserDatails对象生成一个Authentication对象，包含用户信息

/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecurityConfiguration {
    @Resource
    private JwtAuthorizeFilter jwtAuthorizeFilter;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private AccountService accountService;

    /**
     * SpringSecurity 6 配置
     * @param http 配置器
     * @return 自动构建的内置过滤器链
     * @throws Exception 可能的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() // 任何请求都需要验证才可以通过
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login") // 配置登录请求
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess)
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::commence) // 未登录或身份校验失败的处理（无token或token错误或用户被禁用）
                        .accessDeniedHandler(this::accessDeniedHandler) // 用户访问没有权限的接口时的处理（例：普通用户 -> 管理员页面）
                )
                .csrf(AbstractHttpConfigurer::disable) // 禁用csrf
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不使用session的校验方案，将其改为无状态
                )
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class) // 在SpringSecurity用户身份校验前，添加jwt校验的过滤器
                .build();
    }

    /**
     * 登录成功处理handler
     * @param request 请求
     * @param response 响应
     * @param authentication 验证实体
     * @throws IOException 可能的IO异常
     * @throws ServletException 可能的Servlet异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        User user = (User) authentication.getPrincipal();
        Account account = accountService.findAccountByUsername(user.getUsername());
        // 判断账号是否被禁用
        if(account.getActive() == 1) {
            // 登录成功生成jwt令牌
            String token = jwtUtil.createJwt(account.getId(), account.getRole());
            // 封装用户权限信息实体
            AuthorizeVO authorizeVO = new AuthorizeVO(account.getId(), account.getUsername(), account.getRole(), token, jwtUtil.expireTime());
            // 将用户权限信息返回给前端（前端不用解析jwt获取用户信息，前端的jwt只用于给后端身份校验）
            response.getWriter().write(RestBean.success(authorizeVO).asJsonString());
        } else {
            response.getWriter().write(RestBean.forbidden("账号已被禁用").asJsonString());
        }

    }

    /**
     * 退出登录成功handler
     * @param request 请求
     * @param response 响应
     * @param authentication 验证实体
     * @throws IOException 可能的IO异常
     * @throws ServletException 可能的Servlet异常
     */
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        // 从请求头中的Authorization字段读取值，格式为：Bearer Token
        String authorization = request.getHeader("Authorization");
        // 吊销当前用户jwt
        if(jwtUtil.revokeJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        } else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }
    }

    /**
     * 登录检验失败处理handler
     * @param request 请求
     * @param response 响应
     * @param exception 异常
     * @throws IOException 可能的IO异常
     * @throws ServletException 可能的Servlet异常
     */
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.unauthorized("邮箱或密码错误").asJsonString());
    }

    /**
     * 用户访问没有权限的接口时的处理（例：普通用户 -> 管理员页面）
     * @param request 请求
     * @param response 响应
     * @param accessDeniedException 异常
     * @throws IOException 可能的IO异常
     * @throws ServletException 可能的Servlet异常
     */
    public void accessDeniedHandler(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

    /**
     * 未登录和身份校验失败（无token或token错误或用户被禁用）处理handler
     * @param request 请求
     * @param response 响应
     * @param exception 异常
     * @throws IOException 可能的IO异常
     * @throws ServletException 可能的Servlet异常
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
}

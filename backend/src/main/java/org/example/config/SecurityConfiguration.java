package org.example.config;

import org.springframework.context.annotation.Configuration;

// SpringSecurity：一个权限验证框架，负责用户认证登录、用户授权、攻击防护等。
// 配置SpringSecurity
// SpringSecurity工作流程：
// 登录请求交给SecurityFilterChain -> 根据表单password和username创建认证请求UsernamePasswordAuthenticationToken对象
// -> 将UsernamePasswordAuthenticationToken交给认证管理器AuthenticationManager -> 认证管理器将认证请求交给UserDetailService，UserDetailsService 根据 username 从数据库中查找用户信息，并返回一个实现了 UserDetails 接口的用户对象
// -> UsernamePasswordAuthenticationToken 与 UserDetails 对象进行比对，比对成功则返回一个认证成功的 Authentication 对象，比对失败则抛出异常

/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecurityConfiguration {
}

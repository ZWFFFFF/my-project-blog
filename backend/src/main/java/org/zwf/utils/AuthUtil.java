package org.zwf.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuthUtil {
    /**
     * 获取当前登录用户（返回 Spring Security 的 UserDetails 对象）
     * @return 已登录返回 User，未登录返回 null
     */
    public static User getCurrentUser() {
        // 未登录时 Spring Security 默认会创建一个 AnonymousAuthenticationToken
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 检查当前用户是否已登录
     * @return true=已登录，false=未登录
     */
    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * 获取当前登录用户的ID（username 存储的是用户ID）
     * @return 用户ID（未登录返回 null）
     */
    public static Integer getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? Integer.valueOf(user.getUsername()) : null;
    }
}

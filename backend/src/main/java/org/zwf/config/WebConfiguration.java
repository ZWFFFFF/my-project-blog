package org.zwf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class WebConfiguration implements WebMvcConfigurer {
        @Value("${my-config.resource.upload.avatar}")
        private String uploadAvatarPath;
        @Value("${my-config.resource.upload.previewImage}")
        private String uploadPreviewImagePath;

        // 配置SpringSecurity加密和验证用户密码工具实例
        @Bean
        BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        // 将 /** 映射到文件系统的 upload目录
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/avatar/**")
                    .addResourceLocations("file:" + uploadAvatarPath + "/"); // file: 表示资源在文件系统的路径下，后面是具体的路径

            registry.addResourceHandler("/articlePreviewImage/**")
                    .addResourceLocations("file:" + uploadPreviewImagePath + "/");
        }
    }

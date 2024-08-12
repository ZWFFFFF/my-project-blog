package org.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用于处理邮件发送的消息队列监听器
 */
@Slf4j
@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 处理邮件发送
     * @param data 邮件信息
     */
    @RabbitHandler
    public void sendMail(Map<String, Object> data) {
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");

        SimpleMailMessage message = switch (type) {
            case "register" -> createMailMessage(
                    "欢迎您的注册",
                    "您的验证码为：" + code + "，请在5分钟内使用。",
                    email);
            case "reset" -> createMailMessage(
                    "您的密码重置邮件",
                    "您的验证码为：" + code + "，请在5分钟内使用。",
                    email);
            case "login" -> createMailMessage(
                    "欢迎您的登录",
                    "您的验证码为：" + code + "，请在5分钟内使用。",
                    email);
            default -> null;
        };
        if(message == null) return;

        try {
            sender.send(message);
        } catch (Exception e) {
            log.warn("Resolve [{}: {}]", e.getClass().getName(), e.getMessage());
        }
    }

    /**
     * 创建邮件消息实体
     * @param title 邮件标题
     * @param content 邮件内容
     * @param email 收件人邮箱
     * @return 邮件消息实体
     */
    private SimpleMailMessage createMailMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}

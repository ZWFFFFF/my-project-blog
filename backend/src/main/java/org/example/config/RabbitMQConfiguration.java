package org.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置
 */
@Configuration
public class RabbitMQConfiguration {
    @Bean("emailQueue")
    public Queue emailQueue() {
        return QueueBuilder
                .durable("mail")
                .build();
    }

    @Bean("likeQueue")
    public Queue likeQueue() {
        return QueueBuilder
                .durable("like") // 队列名称为 "like"，并且是持久化的
                .build();
    }

    // 消息转换器：将对象转换为JSON格式放入消息队列，从消息队列中取出消息时，将JSON格式的消息转换为对象
    @Bean("jacksonConverter")
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

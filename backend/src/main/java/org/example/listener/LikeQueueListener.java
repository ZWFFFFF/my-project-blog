package org.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ArticleMapper;
import org.example.mapper.CommentMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 点赞队列监听器
 */
@Slf4j
@Component
@RabbitListener(queues = "like")
public class LikeQueueListener {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;

    /**
     * 处理点赞消息
     * @param msg 点赞消息 msg -> {type: "comment" | "article", id: "xxx"}
     */
    @RabbitHandler
    @Transactional
    public void handleLike(Map<String, Object> msg) {
        try {
            String type = (String) msg.get("type");
            Integer id = (Integer) msg.get("id");

            switch (type) {
                case "comment":
                    commentMapper.likeComment(id);
                    break;
                case "article":
                    articleMapper.likeArticle(id);
                    break;
            }
        } catch (Exception e) {
            log.warn("Resolve [{}: {}]", e.getClass().getName(), e.getMessage());
        }
    }

}

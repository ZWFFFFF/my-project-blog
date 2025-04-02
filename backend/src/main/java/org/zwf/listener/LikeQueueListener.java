package org.zwf.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.zwf.mapper.ArticleLikeMapper;
import org.zwf.mapper.ArticleMapper;
import org.zwf.mapper.CommentMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zwf.utils.AuthUtil;

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
    @Resource
    private ArticleLikeMapper articleLikeMapper;

    /**
     * 处理点赞消息
     * @param msg 点赞消息 msg -> {type: "comment" | "article", id: "xxx"}
     */
    @RabbitHandler
    @Transactional
    public void handleLike(Map<String, Object> msg) {
        try {
            String action = (String) msg.get("action");
            String type = (String) msg.get("type");
            Integer id = (Integer) msg.get("id");
            Integer userId = (Integer) msg.get("userId");

            if(action.equals("like")) {
                switch (type) {
                    case "comment":
                        commentMapper.likeComment(id);
                        break;
                    case "article":
                        articleLikeMapper.likeArticle(userId, id);
                        articleMapper.likeArticle(id);
                        break;
                }
            } else if(action.equals("cancelLike")) {
                switch (type) {
                    case "comment":
                        commentMapper.cancelLikeComment(id);
                        break;
                    case "article":
                        articleLikeMapper.cancelLikeArticle(userId, id);
                        articleMapper.cancelLikeArticle(id);
                        break;
                }
            }
        } catch (Exception e) {
            log.warn("Resolve [{}: {}]", e.getClass().getName(), e.getMessage());
        }
    }

}

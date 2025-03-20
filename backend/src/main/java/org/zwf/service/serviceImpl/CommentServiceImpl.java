package org.zwf.service.serviceImpl;

import jakarta.annotation.Resource;
import org.zwf.entity.RestBean;
import org.zwf.entity.dto.Account;
import org.zwf.entity.dto.Article;
import org.zwf.entity.dto.Comment;
import org.zwf.entity.vo.request.AddCommentVO;
import org.zwf.entity.vo.response.AccountVO;
import org.zwf.entity.vo.response.CommentVO;
import org.zwf.exception.BusinessException;
import org.zwf.mapper.ArticleMapper;
import org.zwf.mapper.CommentMapper;
import org.zwf.service.CommentService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 评论相关服务
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 添加评论
     * @param vo 添加评论信息实体
     * @return 响应实体
     */
    @Override
    public RestBean<CommentVO> addComment(AddCommentVO vo) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());
        if (!vo.getUserId().equals(userId)) {
            throw new BusinessException("非法操作");
        }

        // parentId不为空时，检查父评论是否存在
        if (vo.getParentId() != null) {
            Comment parentComment = commentMapper.selectCommentById(vo.getParentId());
            if (parentComment == null) {
                throw new BusinessException("父评论不存在");
            }
        }
        commentMapper.insertComment(vo);
        Comment comment = commentMapper.selectCommentById(vo.getId());
        // 封装评论信息VO
        CommentVO commentVO = toCommentVO(comment);
        return RestBean.success(commentVO);
    }

    /**
     * 获取文章评论
     * @param articleId 文章id
     * @return 响应实体
     */
    @Override
    public RestBean<List<CommentVO>> getComments(Integer articleId) {
        // 获取所有顶级评论
        List<Comment> topLevelComments = commentMapper.selectCommentsByArticleId(articleId);

        // 封装评论信息VO列表
        List<CommentVO> commentVOs = new ArrayList<>();
        for(Comment topLevelComment: topLevelComments) {
            CommentVO topLevelCommentVO = toCommentVO(topLevelComment);

            // 获取该父级评论的所有子级评论并封装为commentVO
            List<Comment> replies = commentMapper.selectRepliesByParentId(topLevelComment.getId());
            List<CommentVO> replyVOs = new ArrayList<>();
            for(Comment reply: replies)  {
                replyVOs.add(toCommentVO(reply));
            }
            // 设置子级评论
            topLevelCommentVO.setReplies(replyVOs);
            // 添加到结果列表
            commentVOs.add(topLevelCommentVO);
        }
        return RestBean.success(commentVOs);
    }

    /**
     * 删除评论
     * @param commentId 评论id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String deleteComment(Integer commentId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());
        Comment comment = commentMapper.selectCommentById(commentId);

        if(comment == null) return "评论不存在";

        Article article = articleMapper.getArticleById(comment.getArticleId());

        if (article == null) return "文章不存在";
        // 检查文章是否属于当前用户，如果是，则可以删除所有评论，否则只能删除自己的评论
        if(!article.getAuthorId().equals(userId)) {
            // 检查评论是否属于当前用户
            if(!comment.getUserId().equals(userId)) {
                return "非法操作";
            }
        }

        // 如果是顶级评论，删除所有子评论
        if(comment.getParentId() == null) {
            commentMapper.deleteCommentsByParentId(commentId);
        }

        commentMapper.deleteComment(commentId);
        return null;
    }

    /**
     * 点赞评论
     * @param commentId 评论id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String likeComment(Integer commentId) {
        Comment comment = commentMapper.selectCommentById(commentId);
        if(comment == null) return "评论不存在";

        // 点赞请求放入消息队列中，由消息队列异步处理点赞
        Map<String, Object> msg = Map.of("type", "comment", "id", commentId);
        amqpTemplate.convertAndSend("like", msg);
        return null;
    }

    /**
     * 封装评论信息VO
     * @param comment 评论信息
     * @return 评论信息VO
     */
    private CommentVO toCommentVO(Comment comment) {
        //获取评论用户信息
        Account account = commentMapper.selectCommentUserInfo(comment.getUserId());
        AccountVO accountVO = new AccountVO();
        accountVO.setId(account.getId());
        accountVO.setUsername(account.getUsername());
        accountVO.setEmail(account.getEmail());
        // 还需要添加Avatar

        // 封装评论信息VO
        CommentVO commentVO = new CommentVO();
        commentVO.setId(comment.getId());
        commentVO.setContent(comment.getContent());
        commentVO.setLike(comment.getLike());
        commentVO.setCreateTime(comment.getCreateTime());
        commentVO.setParentId(comment.getParentId());
        commentVO.setUser(accountVO);
        return commentVO;
    }

}

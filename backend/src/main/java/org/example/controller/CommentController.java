package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.entity.RestBean;
import org.example.entity.vo.request.AddCommentVO;
import org.example.entity.vo.response.CommentVO;
import org.example.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论相关接口
 */
@Validated
@RestController
@RequestMapping("/api/comment")
@Tag(name = "评论相关接口", description = "包含发表评论、删除评论等接口")
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 添加评论
     * @param vo 评论信息实体
     * @return 响应实体
     */
    @PostMapping("/add-comment")
    @Operation(summary = "发表评论")
    public RestBean<CommentVO> addComment(@RequestBody @Valid AddCommentVO vo)  {
        return commentService.addComment(vo);
    }

    /**
     * 获取文章评论
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/get-comments")
    @Operation(summary = "获取评论")
    public RestBean<List<CommentVO>> getComments(@RequestParam @NotNull Integer articleId) {
        return commentService.getComments(articleId);
    }

    /**
     * 删除评论
     * @param commentId 评论id
     * @return 响应实体
     */
    @GetMapping("/delete-comment")
    @Operation(summary = "删除评论")
    public RestBean<Void> deleteComment(@RequestParam @NotNull Integer commentId) {
        return RestBean.messageHandler(() -> commentService.deleteComment(commentId));
    }

    /**
     * 评论点赞
     * @param commentId 评论id
     * @return 响应实体
     */
    @GetMapping("/like-comment")
    @Operation(summary = "点赞评论")
    public RestBean<Void> likeComment(@RequestParam @NotNull Integer commentId) {
        return RestBean.messageHandler(() -> commentService.likeComment(commentId));
    }
}

package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.entity.RestBean;
import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;
import org.example.entity.vo.response.ArticleVO;
import org.example.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于文章管理相关Controller包含新建文章、编辑文章、删除文章、获取文章列表等接口
 */
@Validated
@RestController
@RequestMapping("/api/article")
@Tag(name = "文章管理相关接口", description = "包含新建文章、编辑文章、删除文章、获取文章列表等接口")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 新建文章（默认状态为草稿）
     * @param vo 文章表单实体
     * @return 响应实体
     */
    @PostMapping("/create-article")
    @Operation(summary = "新建文章(存为草稿)")
    public RestBean<Void> createDraft(@RequestBody @Valid CreateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.createArticle(vo, "draft"));
    }

    /**
     * 投稿审核
     * @param vo 文章表单实体
     * @return 响应实体
     */
    @PostMapping("/submission-review")
    @Operation(summary = "投稿审核")
    public RestBean<Void> submission(@RequestBody @Valid CreateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.createArticle(vo, "pending_review"));
    }

    /**
     * 删除文章
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/delete-article")
    @Operation(summary = "删除文章")
    public RestBean<Void> deleteArticle(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.deleteArticle(articleId));
    }

    /**
     * 删除草稿
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/delete-draft")
    @Operation(summary = "删除草稿")
    public RestBean<Void> deleteDraft(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.deleteDraft(articleId));
    }

    /**
     * 更新文章
     * @param vo 更新文章表单实体
     * @return 响应实体
     */
    @PostMapping("/update-article")
    @Operation(summary = "更新文章")
    public RestBean<Void> updateArticle(@RequestBody @Valid UpdateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.updateArticle("approved", vo));
    }

    /**
     * 更新草稿
     * @param vo 更新草稿表单实体
     * @return 响应实体
     */
    @PostMapping("/update-draft")
    @Operation(summary = "更新草稿")
    public RestBean<Void> updateDraft(@RequestBody @Valid UpdateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.updateArticle("draft", vo));
    }

    /**
     * 获取文章信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/article-info")
    @Operation(summary = "获取文章信息")
    public RestBean<ArticleVO> getPublishedArticle(@RequestParam @NotNull Integer articleId) {
        return articleService.getPublishedArticle(articleId);
    }

    /**
     * 获取草稿信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/draft-info")
    @Operation(summary = "获取草稿信息")
    public RestBean<ArticleVO> getDraft(@RequestParam @NotNull Integer articleId) {
        return articleService.getDraft(articleId);
    }

    /**
     * 获取已发布文章列表
     * @return 响应实体
     */
    @GetMapping("/article-list")
    @Operation(summary = "获取文章列表")
    public RestBean<List<ArticleVO>> getArticleList() {
        return articleService.getAllPublishedArticle();
    }

    /**
     * 获取当前用户审核文章列表
     * @return 响应实体
     */
    @GetMapping("/user-review-list")
    @Operation(summary = "获取用户审核文章列表")
    public RestBean<List<ArticleVO>> getUserReviewList() {
        return articleService.getUserReviewArticles();
    }

    /**
     * 获取用户文章列表
     * @param userId 用户id
     * @return 响应实体
     */
    @GetMapping("/user-articles")
    @Operation(summary = "获取用户文章列表")
    public RestBean<List<ArticleVO>> getUserArticles(@RequestParam @NotNull Integer userId)  {
        return articleService.getPublishedArticleByAuthorId(userId);
    }

    /**
     * 获取用户草稿列表
     * @return 响应实体
     */
    @GetMapping("/draft-list")
    @Operation(summary = "获取草稿列表")
    public RestBean<List<ArticleVO>> getDraftList() {
        return articleService.getUserDrafts();
    }

    /**
     * 搜索文章通过关键词
     * @param keyword 关键词
     * @return 响应实体
     */
    @GetMapping("/search")
    @Operation(summary = "搜索文章通过关键词")
    public RestBean<List<ArticleVO>> searchArticleByTitle(@RequestParam @NotNull String keyword) {
        return articleService.getArticleByTitle(keyword);
    }

    /**
     * 获取审核的文章信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/reviewing-article-info")
    @Operation(summary = "获取审核的文章信息")
    public RestBean<ArticleVO> getReviewingArticle(@RequestParam @NotNull Integer articleId) {
        return articleService.getReviewingArticle(articleId);
    }

    /**
     * 开始文章审核
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/start-review")
    @Operation(summary = "开始审核")
    public RestBean<Void> startReview(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.startReview(articleId));
    }

    /**
     * 审核通过
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/approve-review")
    @Operation(summary = "审核通过")
    public RestBean<Void> approveReview(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.approveReview(articleId));
    }

    /**
     * 审核不通过
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/reject-review")
    @Operation(summary = "审核不通过")
    public RestBean<Void> rejectReview(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.rejectReview(articleId));
    }

    /**
     * 重置审核状态
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/reset-reviewing")
    @Operation(summary = "重置审核状态")
    public RestBean<Void> resetReviewing(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.resetReviewing(articleId));
    }
}

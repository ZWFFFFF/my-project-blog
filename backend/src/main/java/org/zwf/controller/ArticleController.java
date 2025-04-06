package org.zwf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import org.zwf.annotation.OperationLog;
import org.zwf.entity.RestBean;
import org.zwf.entity.vo.request.CreateArticleVO;
import org.zwf.entity.vo.request.UpdateArticleVO;
import org.zwf.entity.vo.response.ArticleCollectVo;
import org.zwf.entity.vo.response.ArticleVO;
import org.zwf.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * 获取用户文章列表(已发布和已下架)
     * @param userId 用户id
     * @return 响应实体
     */
    @GetMapping("/user-articles")
    @Operation(summary = "获取用户文章列表(已发布和已下架)")
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
     * 获取待审核文章列表
     * @return 响应实体
     */
    @GetMapping("/pending-review-list")
    @Operation(summary = "获取待审核文章列表")
    public RestBean<List<ArticleVO>> getPendingReviewList() {
        return articleService.getAllPendingReviewArticles();
    }

    /**
     * 获取审核中文章列表
     * @return 响应实体
     */
    @GetMapping("/reviewing-list")
    @Operation(summary = "获取审核中文章列表")
    public RestBean<List<ArticleVO>> getReviewingList() {
        return articleService.getAllReviewingArticles();
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
    @OperationLog(operationType = "REVIEW_APPROVE_ARTICLE")
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
    @OperationLog(operationType = "REVIEW_REJECT_ARTICLE")
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

    /**
     * 文章下架
     * @param articleIds 文章id列表
     * @return 响应实体
     */
    @OperationLog(operationType = "TAKE_DOWN_ARTICLE")
    @PostMapping("/take-down")
    @Operation(summary = "下架文章")
    public RestBean<Void> takeDownArticle(@RequestBody List<Integer> articleIds) {
        return RestBean.messageHandler(() -> articleService.takeDownArticle(articleIds));
    }

    /**
     * 将文章从下架恢复
     * @param articleIds 文章id列表
     * @return 响应实体
     */
    @OperationLog(operationType = "RECOVER_ARTICLE")
    @PostMapping("/recover")
    @Operation(summary = "恢复下架文章")
    public RestBean<Void> recoverArticle(@RequestBody List<Integer> articleIds) {
        return RestBean.messageHandler(() -> articleService.recoverArticle(articleIds));
    }

    /**
     * 获取下架文章列表
     * @return 响应实体
     */
    @GetMapping("/take-down-list")
    @Operation(summary = "获取下架文章列表")
    public RestBean<List<ArticleVO>> getTakeDownList() {
        return articleService.getTakeDownArticleList();
    }

    /**
     * 点赞文章
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/like-toggle")
    @Operation(summary = "点赞文章")
    public RestBean<Void> toggleLikeArticle(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.toggleLikeArticle(articleId));
    }

    /**
     * 收藏/取消收藏文章
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/collect-toggle")
    @Operation(summary = "收藏/取消收藏文章")
    public RestBean<Void> articleCollectToggle(@RequestParam @NotNull Integer articleId) {
        return RestBean.messageHandler(() -> articleService.toggleCollect(articleId));
    }

    /**
     * 获取收藏文章列表
     * @return 响应实体
     */
    @GetMapping("/collect-list")
    @Operation(summary = "获取收藏文章列表")
    public RestBean<List<ArticleCollectVo>> getCollectList(@RequestParam @NotNull Integer userId) {
        return articleService.getCollectedArticles(userId);
    }

    /**
     * 上传文章预览图
     * @param file 文件
     * @return 响应实体
     */
    @PostMapping("/upload-preview-image")
    @Operation(summary = "上传文章预览图")
    public RestBean<Map<String, String>> uploadPreviewImage(@RequestParam @NotNull MultipartFile file) {
        return articleService.uploadPreviewImage(file);
    }
}

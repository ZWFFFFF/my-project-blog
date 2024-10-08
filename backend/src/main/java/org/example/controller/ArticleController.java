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
     * @param vo 新建文章表单实体
     * @return 响应实体
     */
    @PostMapping("/create-article")
    @Operation(summary = "新建文章")
    public RestBean<Void> createDraft(@RequestBody @Valid CreateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.createArticle(vo));
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
     * 更新文章
     * @param vo 更新文章表单实体
     * @return 响应实体
     */
    @PostMapping("/update-article")
    @Operation(summary = "更新文章")
    public RestBean<Void> updateArticle(@RequestBody @Valid UpdateArticleVO vo) {
        return RestBean.messageHandler(() -> articleService.updateArticle(vo));
    }

    /**
     * 获取文章信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @GetMapping("/article-info")
    @Operation(summary = "获取文章信息")
    public RestBean<ArticleVO> getArticle(@RequestParam @NotNull Integer articleId) {
        return articleService.getArticle(articleId);
    }

    /**
     * 获取文章列表
     * @return 响应实体
     */
    @GetMapping("/article-list")
    @Operation(summary = "获取文章列表")
    public RestBean<List<ArticleVO>> getArticleList() {
        return articleService.getAllArticle();
    }

    /**
     * 获取用户文章列表
     * @param userId 用户id
     * @return 响应实体
     */
    @GetMapping("/user-articles")
    @Operation(summary = "获取用户文章列表")
    public RestBean<List<ArticleVO>> getUserArticles(@RequestParam @NotNull Integer userId)  {
        return articleService.getArticleByAuthorId(userId);
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
}

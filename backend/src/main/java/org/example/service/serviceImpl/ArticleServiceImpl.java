package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.Article;
import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;
import org.example.entity.vo.response.ArticleVO;
import org.example.mapper.ArticleMapper;
import org.example.service.AccountService;
import org.example.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章管理相关服务
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private AccountService accountService;
    @Resource
    private ArticleMapper articleMapper;

    /**
     * 新建文章(状态默认为草稿)
     * @param vo 新建草稿表单实体
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String createArticle(CreateArticleVO vo) {
        String title = vo.getTitle();
        String summary = vo.getSummary();
        String content = vo.getContent();
        Integer authorId = vo.getAuthorId();

        if(!accountService.isCurrentUser(authorId)) return "非法操作";
        Article article = new Article(title, summary, content, authorId);
        int insert = articleMapper.insertArticle(article);

        if(insert != 1) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 删除文章
     * @param userId 当前用户id
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String deleteArticle(Integer userId, Integer articleId) {
        if(!accountService.isCurrentUser(userId)) return "非法操作";
        if(!this.isArticlePublisher(userId, articleId)) return "非法操作";

        int delete = articleMapper.deleteArticle(articleId);
        if(delete != 1) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 更新文章
     * @param vo 更新文章表单实体
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String updateArticle(UpdateArticleVO vo) {
        Integer userId = vo.getAuthorId();
        Integer articleId = vo.getId();
        if(!accountService.isCurrentUser(userId)) return "非法操作";
        if(!this.isArticlePublisher(userId, articleId)) return "非法操作";

        String title = vo.getTitle();
        String summary = vo.getSummary();
        String content = vo.getContent();
        int update = articleMapper.updateArticleById(articleId, title, summary, content);
        if(update != 1) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 获取文章信息(若作者账号已注销，则作者名显示为"账号已注销"，id置为null)
     * @param articleId 文章id
     * @return 响应实体
     */
    @Override
    public RestBean<ArticleVO> getArticle(Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return RestBean.argumentNotValid("文章不存在");

        ArticleVO vo = this.toArticleVO(article);
        return RestBean.success(vo);
    }

    /**
     * 获取所有文章
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getAllArticle() {
        List<Article> articles = articleMapper.getAllArticles();

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 根据作者id获取用户所有文章
     * @param authorId 作者id
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getArticleByAuthorId(Integer authorId) {
        List<Article> articles = articleMapper.getArticleByAuthorId(authorId);

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 根据标题获取文章
     * @param title 文章标题
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getArticleByTitle(String title) {
        List<Article> articles = articleMapper.getArticleByTitle(title);

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 判断文章是否属于用户(文章不存在也返回false)
     * @param userId 用户id
     * @param articleId 文章id
     * @return true表示属于，false表示不属于
     */
    private boolean isArticlePublisher(Integer userId, Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return false;
        return article.getAuthorId().equals(userId);
    }

    /**
     * 将文章实体转换为文章信息实体
     * @param article 文章实体
     * @return 文章信息实体
     */
    private ArticleVO toArticleVO(Article article) {
        Integer authorId = article.getAuthorId();
        String author = accountService.getUsernameById(authorId);
        if(author == null) {
            author = "账号已注销";
            authorId = null;
        }

        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setAuthorId(authorId);
        vo.setAuthor(author);
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        vo.setView(article.getView());
        vo.setLike(article.getLike());
        return vo;
    }
}

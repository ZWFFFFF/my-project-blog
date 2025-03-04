package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;
import org.example.entity.vo.response.ArticleVO;

import java.util.List;

public interface ArticleService {
    String createArticle(CreateArticleVO vo, String status);
    String deleteArticle(Integer articleId);
    String deleteDraft(Integer articleId);
    String updateArticle(String type, UpdateArticleVO vo);
    RestBean<ArticleVO> getPublishedArticle(Integer articleId);
    RestBean<ArticleVO> getDraft(Integer articleId);
    RestBean<ArticleVO> getPendingReviewArticle(Integer articleId);
    RestBean<List<ArticleVO>> getAllPublishedArticle();
    RestBean<List<ArticleVO>> getUserDrafts();
    RestBean<List<ArticleVO>> getUserPendingReviewArticles();
    RestBean<List<ArticleVO>> getPublishedArticleByAuthorId(Integer authorId);
    RestBean<List<ArticleVO>> getArticleByTitle(String title);
}

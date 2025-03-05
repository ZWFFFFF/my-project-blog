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
    RestBean<ArticleVO> getReviewingArticle(Integer articleId);
    RestBean<List<ArticleVO>> getAllPublishedArticle();
    RestBean<List<ArticleVO>> getAllPendingReviewArticles();
    RestBean<List<ArticleVO>> getAllReviewingArticles();
    RestBean<List<ArticleVO>> getUserDrafts();
    RestBean<List<ArticleVO>> getUserReviewArticles();
    RestBean<List<ArticleVO>> getPublishedArticleByAuthorId(Integer authorId);
    RestBean<List<ArticleVO>> getArticleByTitle(String title);
    String startReview(Integer articleId);
    String approveReview(Integer articleId);
    String rejectReview(Integer articleId);
    String resetReviewing(Integer articleId);
}

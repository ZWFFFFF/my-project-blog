package org.zwf.service;

import org.springframework.web.multipart.MultipartFile;
import org.zwf.entity.RestBean;
import org.zwf.entity.vo.request.CreateArticleVO;
import org.zwf.entity.vo.request.UpdateArticleVO;
import org.zwf.entity.vo.response.ArticleCollectVo;
import org.zwf.entity.vo.response.ArticleVO;

import java.util.List;
import java.util.Map;

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
    String takeDownArticle(List<Integer> articleIds);
    String recoverArticle(List<Integer> articleIds);
    RestBean<List<ArticleVO>> getTakeDownArticleList();
    String toggleLikeArticle(Integer articleId);
    String toggleCollect(Integer articleId);
    RestBean<List<ArticleCollectVo>> getCollectedArticles(Integer userId);
    RestBean<Map<String, String>> uploadPreviewImage(MultipartFile file);
}

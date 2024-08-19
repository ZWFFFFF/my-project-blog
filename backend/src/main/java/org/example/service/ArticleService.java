package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;
import org.example.entity.vo.response.ArticleVO;

public interface ArticleService {
    String createArticle(CreateArticleVO vo);
    String deleteArticle(Integer userId, Integer articleId);
    String updateArticle(UpdateArticleVO vo);
    RestBean<ArticleVO> getArticle(Integer articleId);
}

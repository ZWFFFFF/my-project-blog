package org.example.service;

import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;

public interface ArticleService {
    String createArticle(CreateArticleVO vo);
    String deleteArticle(Integer userId, Integer articleId);
    String updateArticle(UpdateArticleVO vo);
}

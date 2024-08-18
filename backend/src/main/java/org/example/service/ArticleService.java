package org.example.service;

import org.example.entity.vo.request.CreateArticleVO;

public interface ArticleService {
    String createArticle(CreateArticleVO vo);
    String deleteArticle(Integer userId, Integer articleId);
}

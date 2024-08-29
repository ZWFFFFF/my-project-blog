package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.vo.request.CreateArticleVO;
import org.example.entity.vo.request.UpdateArticleVO;
import org.example.entity.vo.response.ArticleVO;

import java.util.List;

public interface ArticleService {
    String createArticle(CreateArticleVO vo);
    String deleteArticle(Integer articleId);
    String updateArticle(UpdateArticleVO vo);
    RestBean<ArticleVO> getArticle(Integer articleId);
    RestBean<List<ArticleVO>> getAllArticle();
    RestBean<List<ArticleVO>> getArticleByAuthorId(Integer authorId);
    RestBean<List<ArticleVO>> getArticleByTitle(String title);
}

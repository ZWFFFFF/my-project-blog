package org.zwf.mapper;

import org.apache.ibatis.annotations.*;
import org.zwf.entity.dto.ArticleCollect;

import java.util.List;

@Mapper
public interface ArticleCollectMapper {
    @Select("SELECT COUNT(1) > 0 " +
            "FROM article_collect " +
            "WHERE userId = #{userId} AND articleId = #{articleId}")
    boolean isCollected(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Insert("insert into article_collect(userId, articleId) values(#{userId}, #{articleId})")
    void collectArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Delete("delete from article_collect where userId = #{userId} and articleId = #{articleId}")
    void cancelCollectArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Select("select a.id, a.title, a.summary, a.authorId, a.createdAt, a.status, a.view, a.`like`, a.previewImage, b.collectedTime " +
            "from article a, article_collect b where b.articleId = a.id  and b.userId = #{userId}")
    List<ArticleCollect> getUserCollectArticles(@Param("userId") Integer userId);
    @Delete("delete from article_collect where articleId = #{articleId}")
    void deleteArticleCollectByArticleId(@Param("articleId") Integer articleId);
    @Delete("delete from article_collect where userId = #{userId}")
    void deleteArticleCollectByUserId(@Param("userId") Integer userId);
}




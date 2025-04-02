package org.zwf.mapper;

import org.apache.ibatis.annotations.*;
import org.zwf.entity.dto.ArticleCollect;

import java.util.List;

@Mapper
public interface ArticleLikeMapper {
    @Select("SELECT COUNT(1) > 0 " +
            "FROM article_like " +
            "WHERE userId = #{userId} AND articleId = #{articleId}")
    boolean isLiked(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Insert("insert into article_like(userId, articleId) values(#{userId}, #{articleId})")
    void likeArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Delete("delete from article_like where userId = #{userId} and articleId = #{articleId}")
    void cancelLikeArticle(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    @Delete("delete from article_like where articleId = #{articleId}")
    void deleteArticleLikeByArticleId(@Param("articleId") Integer articleId);
}

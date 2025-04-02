package org.zwf.mapper;

import org.apache.ibatis.annotations.*;
import org.zwf.entity.dto.Article;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Insert("insert into article(title, summary, content, authorId) values(#{title}, #{summary}, #{content}, #{authorId})")
    int insertDraft(Article article);
    @Insert("insert into article(title, summary, content, authorId, status) values(#{title}, #{summary}, #{content}, #{authorId}, #{status})")
    int insertArticle(Article article);
    @Delete("delete from article where id = #{id} and status = 'draft'")
    int deleteDraftArticle(Integer id);
    @Delete("delete from article where id = #{id} and status in ('approved', 'take_down')")
    int deletePublishedArticle(Integer id);
    @Select("select * from article where id = #{id}")
    Article getArticleById(Integer id);
    @Select("select id, title, summary, authorId, createdAt, updatedAt, status, view, `like` from article where status = 'approved'")
    List<Article> getAllPublishedArticles();
    @Select("select id, authorId, createdAt, updatedAt, status, view, `like` from article where status = 'pending_review'")
    List<Article> getAllPendingReviewArticles();
    @Select("select id, authorId, createdAt, updatedAt, status, view, `like` from article where status = 'reviewing'")
    List<Article> getAllReviewingArticles();
    @Select("select id, title, summary, authorId, createdAt, updatedAt, status, view, `like` from article where title like concat('%', #{title}, '%') and status = 'approved'")
    List<Article> getArticleByTitle(@Param("title") String title);
    @Select("select id, title, summary, authorId, createdAt, updatedAt, status, view, `like` from article where authorId = #{authorId}")
    List<Article> getArticleByAuthorId(Integer authorId);
    @Update("update article set title = #{title}, summary = #{summary}, content = #{content}, updatedAt = now() where id = #{id}")
    int updateArticleById(@Param("id") Integer id, @Param("title") String title, @Param("summary") String summary, @Param("content") String content);
    @Update("update article set status = #{status} where id = #{id}")
    void updateArticleStatusById(@Param("id") Integer id, @Param("status") String status);
    @Select("select id, authorId, createdAt, updatedAt, status, view, `like` from article where status = 'take_down'")
    List<Article> getAllTakeDownArticles();
    @Update("update article set `like` = `like` + 1 where id = #{id}")
    void likeArticle(Integer id);
    @Update("update article set `like` = `like` - 1 where id = #{id}")
    void cancelLikeArticle(Integer id);
}

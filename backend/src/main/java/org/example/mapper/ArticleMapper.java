package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.dto.Article;

@Mapper
public interface ArticleMapper {
    @Insert("insert into article(title, summary, content, authorId) values(#{title}, #{summary}, #{content}, #{authorId})")
    int insertArticle(Article article);
    @Delete("delete from article where id = #{id}")
    int deleteArticle(Integer id);
    @Select("select * from article where id = #{id}")
    Article getArticleById(Integer id);
    @Update("update article set title = #{title}, summary = #{summary}, content = #{content}, updatedAt = now() where id = #{id}")
    int updateArticleById(Integer id, Article article);
}

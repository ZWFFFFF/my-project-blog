package org.zwf.mapper;

import org.apache.ibatis.annotations.*;
import org.zwf.entity.dto.Account;
import org.zwf.entity.dto.Comment;
import org.zwf.entity.vo.request.AddCommentVO;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("insert into comment (parentId, content, articleId, userId) values (#{parentId}, #{content}, #{articleId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertComment(AddCommentVO vo);
    @Select("select * from comment where articleId = #{articleId} and parentId is null")
    List<Comment> selectCommentsByArticleId(Integer articleId);
    @Select("select * from comment where id = #{id}")
    Comment selectCommentById(Integer id);
    @Select("select * from comment where parentId = #{parentId}")
    List<Comment> selectRepliesByParentId(Integer parentId);
    @Select("select * from account where id = #{userId}")
    Account selectCommentUserInfo(Integer userId);
    @Delete("delete from comment where id = #{id}")
    void deleteComment(Integer id);
    @Delete("delete from comment where parentId = #{parentId}")
    void deleteCommentsByParentId(Integer parentId);
    @Delete("delete from comment where articleId = #{articleId}")
    void deleteCommentsByArticleId(Integer articleId);
    @Update("update comment set `like` = `like` + 1 where id = #{id}")
    void likeComment(Integer id);
    @Update("update comment set `like` = `like` - 1 where id = #{id}")
    void cancelLikeComment(Integer id);
}

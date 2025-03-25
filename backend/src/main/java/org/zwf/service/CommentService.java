package org.zwf.service;

import org.zwf.entity.RestBean;
import org.zwf.entity.vo.request.AddCommentVO;
import org.zwf.entity.vo.response.CommentVO;

import java.util.List;

public interface CommentService {
    RestBean<CommentVO> addComment(AddCommentVO vo);
    RestBean<List<CommentVO>> getComments(Integer articleId);
    String deleteComment(Integer commentId);
    String likeComment(Integer commentId);
    String dislikeComment(Integer commentId);
}

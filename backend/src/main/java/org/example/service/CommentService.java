package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.vo.request.AddCommentVO;
import org.example.entity.vo.response.CommentVO;

import java.util.List;

public interface CommentService {
    RestBean<CommentVO> addComment(AddCommentVO vo);
    RestBean<List<CommentVO>> getComments(Integer articleId);
    String deleteComment(Integer commentId);
    String likeComment(Integer commentId);
}

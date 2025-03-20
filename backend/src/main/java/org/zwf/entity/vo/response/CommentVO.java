package org.zwf.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 评论信息实体封装
 */
@Data
public class CommentVO {
    private Integer id;
    private Integer parentId;
    private String content;
    private AccountVO user;
    private Date createTime;
    private Integer like;
    private List<CommentVO> replies;
}

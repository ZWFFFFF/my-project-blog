package org.zwf.entity.dto;


import lombok.Data;
import java.util.Date;

/**
 * 评论实体类
 */
@Data
public class Comment {
    private Integer id;
    private Integer parentId;
    private String content;
    private Integer articleId;
    private Integer userId;
    private Integer like;
    private Date createTime;
}

package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 文章信息实体封装
 */
@Data
public class ArticleVO {
    private Integer id;
    private String title;
    private String summary;
    private String content; // list中的文章内容为null
    private Integer authorId; // 账号已注销，设为null
    private String author; // 账号已注销，设为“账号已注销”
    private Date createdAt;
    private Date updatedAt;
    private Integer view;
    private Integer like;
}

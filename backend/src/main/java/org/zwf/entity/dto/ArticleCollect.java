package org.zwf.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 文章收藏实体类
 */
@Data
public class ArticleCollect {
    private Integer id;
    private String title;
    private String summary;
    private Integer authorId;
    private Date createdAt;
    private String status;
    private Integer view;
    private Integer like;
    private Date collectedTime;
}

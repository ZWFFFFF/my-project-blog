package org.zwf.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 文章收藏实体封装
 */
@Data
public class ArticleCollectVo {
    private Integer id;
    private String title;
    private String summary;
    private Integer authorId;
    private String author;
    private Date createdAt;
    private Integer view;
    private Integer like;
    private String status;
    private Date collectedTime;
    private String previewImage;
}

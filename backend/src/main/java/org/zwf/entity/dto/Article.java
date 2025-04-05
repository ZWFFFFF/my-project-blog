package org.zwf.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据库中文章信息
 */
@Data
public class Article {
    private Integer id;
    private String title;
    private String summary;
    private String content;
    private Integer authorId;
    private Date createdAt;
    private Date updatedAt;
    private String status; // 'draft'(default), 'pending_review', 'reviewing', 'approved', 'take_down'
    private Integer view;
    private Integer like;
    private String previewImage;
}

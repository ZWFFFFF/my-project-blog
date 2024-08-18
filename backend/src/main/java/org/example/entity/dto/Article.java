package org.example.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据库中文章信息
 */
@Data
@NoArgsConstructor
public class Article {
    private Integer id;
    private String title;
    private String summary;
    private String content;
    private Integer authorId;
    private Date createdAt;
    private Date updatedAt;
    private String status; // 'draft'(default), 'pending_review', 'reviewing', 'approved', 'archived'
    private Integer view;
    private Integer like;

    public Article(String title, String summary, String content, Integer authorId) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.authorId = authorId;
    }
}

package org.example.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新建文章表单实体
 */
@Data
public class CreateArticleVO {
    @NotBlank
    private String title;
    @NotBlank
    private String summary;
    @NotBlank
    private String content;
    @NotNull
    private Integer authorId;
}

package org.example.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新文章表单实体
 */
@Data
@NoArgsConstructor
public class UpdateArticleVO {
    @NotNull
    private Integer id;
    @NotBlank
    private String title;
    @NotBlank
    private String summary;
    @NotBlank
    private String content;
    @NotNull
    private Integer authorId;
}

package org.zwf.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发表评论实体
 */
@Data
public class AddCommentVO {
    private Integer id; // 不用设置，由数据库生成
    private Integer parentId; // 父级评论为null
    @NotNull
    private Integer articleId;
    @NotNull
    private Integer userId;
    @NotBlank
    private String content;
}

package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: akkkka114514
 * @create: 2025-04-23 07:20
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ActivityComment implements Serializable {

    private static final long serialVersionUID = 1L;
    // 评论类型,mysql enum
    public static final String ROOT = "root";
    public static final String SUB = "sub";

    @TableId(value = "id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer id;


    //评论用户id
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(groups = ScopeInsert.class)
    @Null(groups = ScopeUpdate.class)
    private Integer userId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(groups = {ScopeInsert.class, ScopeUpdate.class})
    @Null(groups = ScopeUpdate.class)
    private Integer activityId;

    //被评论的评论的id,null即为最顶层根评论
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = ScopeUpdate.class)
    private Integer toCommentId;

    @NotEmpty(groups = ScopeInsert.class)
    private String content;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = ScopeUpdate.class)
    private Integer hot;

    @NotNull(groups = ScopeInsert.class)
    @FutureOrPresent
    @Null(groups = ScopeUpdate.class)
    private LocalDateTime createTime;

    //最顶层评论id，为null即为顶层
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = ScopeUpdate.class)
    private Integer rootId;

    @Min(0)
    @Max(1)
    private Byte isDeleted;

    @NotNull(groups = ScopeUpdate.class)
    @FutureOrPresent
    private LocalDateTime updateTime;
}

package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer id;

    /**
     * 用户名
     */
    @NotEmpty(groups = ScopeInsert.class)
    private String username;

    /**
     * 密码md5
     */
    @NotEmpty(groups = ScopeInsert.class)
    private String password;

    /**
     * 创建时间
     */
    @PastOrPresent
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * fun学分
     */
    @Digits(integer = 1, fraction = 1)
    private float funScore;

    /**
     * 用户所属学校id
     */
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer schoolId;

    /**
     * 用户所属学院学校id
     */
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer collegeId;

    /**
     * 电话号码
     * */
    private String phone;

    /**
     * 0->未删除，1->已删除
     * */
    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte isDeleted;
}

package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author Zoe Wu
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("user_permission")
public class UserPermission implements Serializable {
    @TableId(value = "user_id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer userId;
    @Min(0)
    @Max(1)
    private byte manageSignIn;
    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte publishActivity;
    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte audit;
    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte editSchool;
    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte grantPermission;
}

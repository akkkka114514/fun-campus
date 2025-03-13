package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.TableName;
import javax.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

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
@TableName("activity_user_map")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityUserMap implements Serializable {

    
    private static final long serialVersionUID = 1L;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer activityId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer userId;

    /**
     * 是否已签到，0->否，1->是
     * */
    @Min(0)
    @Max(1)
    @NotNull(groups = {ScopeInsert.class})
    private Integer isSignIn;
}

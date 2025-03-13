package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.IdType;
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
public class Activity implements Serializable {

    
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer id;

    /**
     * 标题
     */
    @NotEmpty(groups = ScopeInsert.class)
    private String title;

    /**
     * 1->等待报名，2->等待签到,3->活动结束
     */
    @Min(1)
    @Max(3)
    private Byte status;

    /**
     * 开始报名时间
     */
    @FutureOrPresent
    @NotNull(groups = ScopeInsert.class)
    private LocalDateTime enrollStartTime;

    /**
     * 结束报名时间
     */
    @Future
    @NotNull(groups = ScopeInsert.class)
    private LocalDateTime enrollEndTime;

    /**
     * 活动开始时间
     */
    @Future
    @NotNull(groups = ScopeInsert.class)
    private LocalDateTime activityStartTime;

    /**
     * 活动结束时间
     */
    @Future
    @NotNull(groups = ScopeInsert.class)
    private LocalDateTime activityEndTime;

    /**
     * 活动地址
     */
    @NotEmpty(groups = ScopeInsert.class)
    private String activityPosition;

    /**
     * 每个人完成活动后可以获得的学分
     */
    @Digits(integer = 1, fraction = 1)
    @NotNull(groups = ScopeInsert.class)
    private float scoreCanGet;

    /**
     * 可以报名的人数限制
     */
    @Max(Integer.MAX_VALUE)
    @Min(1)
    @NotNull(groups = ScopeInsert.class)
    private Integer enrollNumLimit;

    /**
     * 活动所属学校
     */
    @NotNull(groups = ScopeInsert.class)
    @Max(Integer.MAX_VALUE)
    @Min(0)
    private Integer activitySchoolId;
    /**
     * 发起活动的组织id
     */
    @NotNull(groups = ScopeInsert.class)
    @Max(Integer.MAX_VALUE)
    @Min(0)
    private Integer activityOrganizerId;

    /**
     * 0->未删除，1->已删除
     * */
    @Min(0)
    @Max(1)
    private Byte isDeleted;

    /**
     * 已报名人数
     * */
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer enrollNum;
}

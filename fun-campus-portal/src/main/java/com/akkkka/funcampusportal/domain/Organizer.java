package com.akkkka.funcampusportal.domain;

import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import javax.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Organizer implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer id;

    @NotEmpty(groups = ScopeInsert.class)
    private String organizerName;

    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    private Integer schoolId;
    /**
     * 学校的学院id
     * 为null则说明不是一个学院而是学校组织
     * */
    @NotNull(groups = ScopeInsert.class)
    private Integer collegeId;

    @NotNull(groups = ScopeInsert.class)
    @Min(0)
    @Max(1)
    private byte isDeleted;

    @NotNull(groups = ScopeInsert.class)
    @FutureOrPresent
    private LocalDateTime createTime;


    @NotNull(groups = ScopeUpdate.class)
    @FutureOrPresent
    private LocalDateTime updateTime;
}

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
@AllArgsConstructor
@NoArgsConstructor
public class School implements Serializable {

    
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Null(groups = {ScopeInsert.class, ScopeUpdate.class})
    private Integer id;

    /**
     * 学校名
     */
    @NotEmpty(groups = ScopeInsert.class)
    private String schoolName;

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

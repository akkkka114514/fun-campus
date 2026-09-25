package com.akkkka.common.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-02-12 16:00
 */
@Document(collection = "disableUser")
@Data
public class DisableUserDocument {
    private String system;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean deleted;
}

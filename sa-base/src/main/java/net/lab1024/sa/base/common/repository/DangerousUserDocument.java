package net.lab1024.sa.base.common.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-02-12 15:44
 */
@Document(collection = "dangerousUser")
@Data
public class DangerousUserDocument {
    private String System;
    private Long userId;
    private String ip;
    private String detailMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean deleted;
}

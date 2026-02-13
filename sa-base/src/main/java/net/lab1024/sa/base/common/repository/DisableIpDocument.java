package net.lab1024.sa.base.common.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-02-12 16:04
 */
@Document(collection = "disableIp")
@Data
public class DisableIpDocument {
    private String ip;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean deleted;
}

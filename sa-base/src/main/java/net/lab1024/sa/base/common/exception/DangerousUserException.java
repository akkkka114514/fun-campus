package net.lab1024.sa.base.common.exception;

import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-02-11 19:35
 */
@Data
public class DangerousUserException extends RuntimeException{
    private String system;
    private Long userId;
    private String ip;
    private String detailMsg;

    public DangerousUserException(Long userId ,String ip, String detailMsg,String system){
        this.userId=userId;
        this.ip=ip;
        this.detailMsg=detailMsg;
        this.system=system;
    }

    public DangerousUserException() {
    }
}

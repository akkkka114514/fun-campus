package net.lab1024.sa.base.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

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

    @AllArgsConstructor
    @Getter
    public enum System{
        PORTAL("portal"),
        BACKEND("backend");

        private final String systemName;
    }

    public DangerousUserException(Long userId ,String ip, String detailMsg,System system){
        this.userId=userId;
        this.ip=ip;
        this.detailMsg=detailMsg;
        this.system=system.getSystemName();
    }

}

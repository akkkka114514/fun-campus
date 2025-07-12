package com.akkkka.funcampusapiportal.vo;

import com.akkkka.funcampusapiportal.domain.StudentUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: akkkka114514
 * @create: 2025-04-23 09:38
 * @description: 如评论场景只显示部分信息的user，未来可能加上avatar
 */
@Data
@NoArgsConstructor
public class SimpleUserVO implements Serializable {
    private Integer id;
    private String username;

    public SimpleUserVO(StudentUser user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}

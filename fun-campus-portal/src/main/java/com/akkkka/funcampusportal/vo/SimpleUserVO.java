package com.akkkka.funcampusportal.vo;

import com.akkkka.funcampusportal.domain.User;
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

    public SimpleUserVO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}

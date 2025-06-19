package com.akkkka.funcampusportal.vo;

import com.akkkka.funcampusportal.domain.ActivityComment;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: akkkka114514
 * @create: 2025-06-19 14:56
 * @description:
 */
@Data
public abstract class ActivityCommentBaseVO implements Serializable {
    protected Integer id;

    protected Integer activityId;

    protected SimpleUserVO userInfo;

    protected Integer toCommentId;

    protected Integer rootId;

    protected String content;

    protected Integer hot;

    protected LocalDateTime createTime;
    public ActivityComment toActivityComment(){
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(this.activityId);
        comment.setContent(this.content);
        comment.setToCommentId(this.toCommentId);
        comment.setUserId(this.userInfo.getId());
        comment.setHot(0);
        comment.setCreateTime(this.createTime);
        comment.setRootId(this.rootId);
        return comment;
    }
}

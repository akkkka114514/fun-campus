package com.akkkka.funcampusportal.vo;

import com.akkkka.funcampusportal.domain.ActivityComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-04-23 09:11
 * @description:
 */
@NoArgsConstructor
public class ActivityCommentReplyVO extends ActivityCommentBaseVO implements Serializable {

    public ActivityCommentReplyVO(ActivityComment comment, SimpleUserVO userInfo, SimpleUserVO replyUserInfo, List<ActivityCommentReplyVO> replyList) {
        this.id = comment.getId();
        this.activityId = comment.getActivityId();
        this.userInfo = userInfo;
        this.toCommentId = comment.getToCommentId();
        this.rootId = comment.getRootId();
        this.content = comment.getContent();
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
    }
    public ActivityCommentReplyVO(ActivityComment comment) {
        this.id = comment.getId();
        this.activityId = comment.getActivityId();
        this.userInfo = null;
        this.toCommentId = comment.getToCommentId();
        this.rootId = comment.getRootId();
        if(comment.getIsDeleted()==1){
            this.content="此评论已被删除";
        }else {
            this.content = comment.getContent();
        }
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
    }
}

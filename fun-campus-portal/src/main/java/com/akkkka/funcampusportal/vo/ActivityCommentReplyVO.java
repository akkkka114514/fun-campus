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
@Data
public class ActivityCommentReplyVO implements Serializable {
    private Integer id;

    private Integer activityId;

    /**
     * 评论用户信息
     */
    private SimpleUserVO userInfo;

    private Integer toCommentId;

    private Integer rootId;

    private String content;

    private Integer hot;


    private LocalDateTime createTime;

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
        this.content = comment.getContent();
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
    }
}

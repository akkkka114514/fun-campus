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
 * @description:一个活动下的根评论
 */
@Data
@NoArgsConstructor
public class ActivityCommentRootVO implements Serializable {
    private Integer id;

    private Integer activityId;

    /**
     * 评论用户信息
     */
    private SimpleUserVO userInfo;

    private String content;

    private Integer hot;

    private LocalDateTime createTime;

    private List<ActivityCommentReplyVO> replyList;

    private Integer rootId;

    public ActivityCommentRootVO(ActivityComment comment, SimpleUserVO userInfo, List<ActivityCommentReplyVO> replyList){
        this.activityId = comment.getActivityId();
        this.userInfo = userInfo;
        this.content = comment.getContent();
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
        this.replyList = replyList;
    }
    public ActivityCommentRootVO(ActivityComment comment){
        this.activityId = comment.getActivityId();
        this.userInfo = null;
        this.content = comment.getContent();
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
        this.replyList = null;
    }

}

package com.akkkka.funcampusapiportal.vo;

import com.akkkka.funcampusapiportal.domain.ActivityComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-04-23 09:11
 * @description:一个活动下的根评论
 */
@NoArgsConstructor
@Getter
@Setter
public class ActivityCommentRootVO extends ActivityCommentBaseVO implements Serializable {

    private List<ActivityCommentReplyVO> replyList;


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
        if(comment.getIsDeleted()==1){
            this.content="此评论已被删除";
        }else {
            this.content = comment.getContent();
        }
        this.hot = comment.getHot();
        this.createTime = comment.getCreateTime();
        this.replyList = null;
    }



}

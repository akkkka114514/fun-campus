package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.ActivityComment;
import com.akkkka.funcampusportal.vo.ActivityCommentBaseVO;
import com.akkkka.funcampusportal.vo.ActivityCommentReplyVO;
import com.akkkka.funcampusportal.vo.ActivityCommentRootVO;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-04-23 07:34
 * @description:
 */
public interface IActivityCommentService extends IService<ActivityComment> {

    List<ActivityCommentRootVO> getByActivityId(Integer activityId);

    void addComment(ActivityCommentBaseVO commentVO);

    Integer getActivityCommentCount(Integer activityId);

    void deleteComment(Integer commentId);
}

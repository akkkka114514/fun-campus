package com.akkkka.funcampusportal.service.impl;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.funcampusportal.domain.ActivityComment;
import com.akkkka.funcampusportal.mapper.ActivityCommentMapper;
import com.akkkka.funcampusportal.service.IActivityCommentService;
import com.akkkka.funcampusportal.service.IActivityService;
import com.akkkka.funcampusportal.service.IUserService;
import com.akkkka.funcampusportal.vo.ActivityCommentBaseVO;
import com.akkkka.funcampusportal.vo.ActivityCommentReplyVO;
import com.akkkka.funcampusportal.vo.ActivityCommentRootVO;
import com.akkkka.funcampusportal.vo.SimpleUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
*@author: akkkka114514
*@create: 2025-04-23 07:38
*@description: 
*/
@Service
public class ActivityCommentServiceImpl extends ServiceImpl<ActivityCommentMapper, ActivityComment> implements IActivityCommentService{

    @Resource
    private IUserService userService;
    @Resource
    private IActivityService activityService;
    @Override
    public List<ActivityCommentRootVO> getByActivityId(Integer activityId) {
        //获取一个activity下的所有评论，包括根评论和子评论
        LambdaQueryWrapper<ActivityComment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //保证评论顺序从新到旧
        lambdaQueryWrapper.eq(ActivityComment::getActivityId,activityId)
                        .orderByDesc(ActivityComment::getCreateTime);
        List<ActivityComment> comments = this.list(lambdaQueryWrapper);
        if(comments.isEmpty()){
            return List.of();
        }
        //获取查出来的评论里的userId，去查user表
        List<Integer> userIds = comments.stream()
                .map(ActivityComment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer,SimpleUserVO> simpleUsers =
                userService.listByIds(userIds)
                .stream()
                .map(SimpleUserVO::new)
                .collect(
                        Collectors.toMap(SimpleUserVO::getId, simpleUserVO -> simpleUserVO)
                );

        List<ActivityCommentRootVO> result = new ArrayList<>();
        Map<Integer,List<ActivityCommentReplyVO>> rootMap = new HashMap<>();
        for (ActivityComment comment : comments) {
            if(comment.getRootId() == null){
                ActivityCommentRootVO activityCommentRootVO = new ActivityCommentRootVO(comment);
                activityCommentRootVO.setUserInfo(simpleUsers.get(comment.getUserId()));
                activityCommentRootVO.setReplyList(new ArrayList<>());
                result.add(activityCommentRootVO);
                rootMap.put(comment.getId(),new ArrayList<>());
            }else{
                ActivityCommentReplyVO activityCommentReplyVO = new ActivityCommentReplyVO(comment);
                activityCommentReplyVO.setUserInfo(simpleUsers.get(comment.getUserId()));
                //由于排序顺序，所以插入子评论时根评论一定存在
                rootMap.get(comment.getRootId()).add(activityCommentReplyVO);
            }
        }
        //组装子评论
        for (Map.Entry<Integer, List<ActivityCommentReplyVO>> e: rootMap.entrySet()){
            for (ActivityCommentRootVO activityCommentRootVO : result) {
                if(activityCommentRootVO.getId().equals(e.getKey())){
                    activityCommentRootVO.setReplyList(e.getValue());
                }
            }
        }
        return result;
    }
    /** 
    * <p>
    * description: 
    * </p>
    *
    * @param commentVO
    * @return:  
    * @author: akkkka114514
    * @date: 15:15:33 2025-06-19 
    */ 
    @Override
    public void addComment(ActivityCommentBaseVO commentVO) {
        ActivityComment comment = commentVO.toActivityComment();
        if(activityService.getById(comment.getActivityId())==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"activity");
        }
        if(userService.getById(comment.getUserId())==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"user");
        }
        this.save(comment);
    }
    /** 
    * <p>
    * description: 
    * </p>
    *
    * @param activityId
    * @return:  
    * @author: akkkka114514
    * @date: 18:41:22 2025-06-19 
    */ 
    @Override
    public Integer getActivityCommentCount(Integer activityId) {
        if(activityService.getById(activityId)==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"activity");
        }
        LambdaQueryWrapper<ActivityComment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return Math.toIntExact(this.count(lambdaQueryWrapper.eq(ActivityComment::getActivityId, activityId)));
    }
    /**
    * <p>
    * description:
    * </p>
    *
    * @param commentId
    * @return:
    * @author: akkkka114514
    * @date: 18:52:18 2025-06-19
    */
    @Override
    public void deleteComment(Integer commentId) {
        ActivityComment comment = this.getById(commentId);
        if(comment==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"comment");
        }
        if(comment.getIsDeleted()!=1){
            comment.setIsDeleted((byte) 1);
            this.updateById(comment);
        }
    }
}

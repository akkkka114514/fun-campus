package com.akkkka.module.support.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.module.support.feedback.dao.FeedbackDao;
import com.akkkka.module.support.feedback.domain.FeedbackAddForm;
import com.akkkka.module.support.feedback.domain.FeedbackEntity;
import com.akkkka.module.support.feedback.domain.FeedbackQueryForm;
import com.akkkka.module.support.feedback.domain.FeedbackVO;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 意见反馈
 *
 * @Author 1024创新实验室: 开云
 * @Date 2022-08-11 20:48:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class FeedbackService {

    @Resource
    private FeedbackDao feedbackDao;

    /**
     * 分页查询
     *
     */
    public ResponseDTO<PageResult<FeedbackVO>> query(FeedbackQueryForm queryForm) {
        Page page = SmartPageUtil.convert2PageQuery(queryForm);
        List<FeedbackVO> list = feedbackDao.queryPage(page, queryForm);
        PageResult<FeedbackVO> pageResultDTO = SmartPageUtil.convert2PageResult(page, list);
        if (pageResultDTO.getEmptyFlag()) {
            return ResponseDTO.ok(pageResultDTO);
        }
        return ResponseDTO.ok(pageResultDTO);
    }

    /**
     * 新建
     */
    public ResponseDTO<String> add(FeedbackAddForm addForm, RequestUser requestUser) {
        FeedbackEntity feedback = SmartBeanUtil.copy(addForm, FeedbackEntity.class);
        feedback.setUserType(requestUser.getUserType().getValue());
        feedback.setUserId(requestUser.getUserId());
        feedback.setUserName(requestUser.getUserName());
        feedbackDao.insert(feedback);
        return ResponseDTO.ok();
    }
}

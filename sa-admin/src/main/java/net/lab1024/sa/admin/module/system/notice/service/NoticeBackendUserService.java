package net.lab1024.sa.admin.module.system.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import net.lab1024.sa.admin.module.system.notice.constant.NoticeVisibleRangeDataTypeEnum;
import net.lab1024.sa.admin.module.system.notice.dao.NoticeDao;
import net.lab1024.sa.admin.module.system.notice.domain.form.NoticeBackendUserQueryForm;
import net.lab1024.sa.admin.module.system.notice.domain.form.NoticeViewRecordQueryForm;
import net.lab1024.sa.admin.module.system.notice.domain.vo.*;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * 后台用户查看 通知。公告
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class NoticeBackendUserService {

    @Resource
    private NoticeDao noticeDao;

    @Resource
    private NoticeService noticeService;

    @Resource
    private BackendUserService backendUserService;

    /**
     * 查询我的 通知、公告清单
     */
    public ResponseDTO<PageResult<NoticeBackendUserVO>> queryList(Long requestBackendUserId, NoticeBackendUserQueryForm noticeBackendUserQueryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(noticeBackendUserQueryForm);
        List<NoticeBackendUserVO> noticeList = null;
        boolean isDeleted = false;
        //只查询未读的
        if (noticeBackendUserQueryForm.getNotViewFlag() != null && noticeBackendUserQueryForm.getNotViewFlag()) {
            noticeList = noticeDao.queryBackendUserNotViewNotice(page,
                    requestBackendUserId,
                    noticeBackendUserQueryForm,
                    isDeleted,
                    NoticeVisibleRangeDataTypeEnum.BACKEND_USER.getValue());
        } else {
            // 查询全部
            noticeList = noticeDao.queryBackendUserNotice(page,
                    requestBackendUserId,
                    noticeBackendUserQueryForm,
                    isDeleted,
                    NoticeVisibleRangeDataTypeEnum.BACKEND_USER.getValue());
        }
        // 设置发布日期
        noticeList.forEach(notice -> notice.setPublishDate(notice.getPublishTime().toLocalDate()));

        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, noticeList));
    }


    /**
     * 查询我的 待查看的 通知、公告清单
     */
    public ResponseDTO<NoticeDetailVO> view(Long requestBackendUserId, Long noticeId, String ip, String userAgent) {
        NoticeUpdateFormVO updateFormVO = noticeService.getUpdateFormVO(noticeId);
        if (updateFormVO == null || Boolean.TRUE.equals(updateFormVO.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("通知公告不存在");
        }

        BackendUserEntity backendUserEntity = backendUserService.getById(requestBackendUserId);
        if (!updateFormVO.getAllVisibleFlag()
                && !checkVisibleRange(updateFormVO.getVisibleRangeList(), requestBackendUserId)) {
            return ResponseDTO.userErrorParam("对不起，您没有权限查看内容");
        }

        NoticeDetailVO noticeDetailVO = SmartBeanUtil.copy(updateFormVO, NoticeDetailVO.class);
        long viewCount = noticeDao.viewRecordCount(noticeId, requestBackendUserId);
        if (viewCount == 0) {
            noticeDao.insertViewRecord(noticeId, requestBackendUserId, ip, userAgent, 1);
            // 该后台用户对于这个通知是第一次查看 页面浏览量+1 用户浏览量+1
            noticeDao.updateViewCount(noticeId, 1, 1);
            noticeDetailVO.setPageViewCount(noticeDetailVO.getPageViewCount() + 1);
            noticeDetailVO.setUserViewCount(noticeDetailVO.getUserViewCount() + 1);
        } else {
            noticeDao.updateViewRecord(noticeId, requestBackendUserId, ip, userAgent);
            // 该后台用户对于这个通知不是第一次查看 页面浏览量+1 用户浏览量+0
            noticeDao.updateViewCount(noticeId, 1, 0);
            noticeDetailVO.setPageViewCount(noticeDetailVO.getPageViewCount() + 1);
        }

        return ResponseDTO.ok(noticeDetailVO);
    }

    /**
     * 校验是否有查看权限的范围
     *
     */
    public boolean checkVisibleRange(List<NoticeVisibleRangeVO> visibleRangeList, Long backendUserId) {
        // 后台用户范围
        return visibleRangeList
                .stream()
                .anyMatch(
                        e ->
                                NoticeVisibleRangeDataTypeEnum.BACKEND_USER.equalsValue(e.getDataType())
                                && Objects.equals(e.getDataId(), backendUserId));
    }

    /**
     * 分页查询  查看记录
     */
    public PageResult<NoticeViewRecordVO> queryViewRecord(NoticeViewRecordQueryForm noticeViewRecordQueryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(noticeViewRecordQueryForm);
        List<NoticeViewRecordVO> noticeViewRecordList = noticeDao.queryNoticeViewRecordList(page, noticeViewRecordQueryForm);
        return SmartPageUtil.convert2PageResult(page, noticeViewRecordList);
    }
}

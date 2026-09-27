package com.akkkka.admin.module.system.notice.service;

import com.akkkka.admin.module.system.notice.dao.NoticeDao;
import com.akkkka.admin.module.system.notice.domain.form.PortalNoticeQueryForm;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeDetailVO;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeVO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartPageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告 门户查看服务：当前登录的前台用户（学生/组织者）浏览校内公告
 * <p>
 * 门户用户无后台用户体系，可见性收敛为全员可见（all_visible_flag=true）且已发布（publish_time &lt; now()）；
 * 查看详情仅累加页面浏览量（不写 t_notice_view_record，该表按后台用户维度）
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Service
@AllArgsConstructor
public class PortalNoticeService {

    private final NoticeDao noticeDao;

    /**
     * 分页查询 门户用户可见的通知公告
     */
    public ResponseDTO<PageResult<PortalNoticeVO>> queryNotice(PortalNoticeQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PortalNoticeVO> noticeList = noticeDao.queryPortalNotice(page, queryForm);
        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, noticeList));
    }

    /**
     * 查询 门户用户可见的通知公告详情，并累加页面浏览量
     */
    public ResponseDTO<PortalNoticeDetailVO> detail(Long noticeId) {
        PortalNoticeDetailVO detailVO = noticeDao.getPortalNoticeById(noticeId);
        if (detailVO == null) {
            return ResponseDTO.userErrorParam("通知公告不存在");
        }
        noticeDao.updateViewCount(noticeId, 1, 0);
        detailVO.setPageViewCount(detailVO.getPageViewCount() == null ? 1 : detailVO.getPageViewCount() + 1);
        return ResponseDTO.ok(detailVO);
    }

}

package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.dao.OrganizerActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity.OrganizerActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.vo.OrganizerActivityVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 运营者发布活动的对应关系 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */

@Service
public class OrganizerActivityService {

    @Resource
    private OrganizerActivityDao organizerActivityDao;

    /**
     * 分页查询
     */
    public PageResult<OrganizerActivityVO> queryPage(OrganizerActivityQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrganizerActivityVO> list = organizerActivityDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrganizerActivityAddForm addForm) {
        OrganizerActivityEntity organizerActivityEntity = SmartBeanUtil.copy(addForm, OrganizerActivityEntity.class);
        organizerActivityDao.insert(organizerActivityEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(OrganizerActivityUpdateForm updateForm) {
        OrganizerActivityEntity organizerActivityEntity = SmartBeanUtil.copy(updateForm, OrganizerActivityEntity.class);
        organizerActivityDao.updateById(organizerActivityEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        organizerActivityDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        organizerActivityDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}

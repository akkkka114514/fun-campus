package net.lab1024.sa.admin.module.business.funcampus.gradeInfo.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.dao.GradeInfoDao;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.entity.GradeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.GradeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.SimpleGradeInfoVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 年级信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@Slf4j
@Service
public class GradeInfoService {

    @Resource
    private GradeInfoDao gradeInfoDao;

    /**
     * 分页查询
     */
    public PageResult<GradeInfoVO> queryPage(GradeInfoQueryForm queryForm) {
        log.info("GradeInfoService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<GradeInfoVO> list = gradeInfoDao.queryPage(page, queryForm);
        log.info("GradeInfoService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(GradeInfoAddForm addForm) {
        log.info("GradeInfoService.add called, addForm={}", addForm);
        GradeInfoEntity gradeInfoEntity = SmartBeanUtil.copy(addForm, GradeInfoEntity.class);
        int result = gradeInfoDao.insert(gradeInfoEntity);
        if (result > 0) {
            log.info("GradeInfoService.add success: new record created with id={}", gradeInfoEntity.getId());
        } else {
            log.error("GradeInfoService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(GradeInfoUpdateForm updateForm) {
        log.info("GradeInfoService.update called, updateForm={}", updateForm);
        GradeInfoEntity gradeInfoEntity = SmartBeanUtil.copy(updateForm, GradeInfoEntity.class);
        int result = gradeInfoDao.updateById(gradeInfoEntity);
        if (result > 0) {
            log.info("GradeInfoService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("GradeInfoService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("GradeInfoService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("GradeInfoService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = gradeInfoDao.batchUpdateDeleted(idList, true);
        log.info("GradeInfoService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("GradeInfoService.delete called, id={}", id);
        if (null == id){
            log.warn("GradeInfoService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = gradeInfoDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("GradeInfoService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("GradeInfoService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }

    /**
     * 获取所有
     */
    public List<SimpleGradeInfoVO> getAll() {
        log.debug("GradeInfoService.getAll called");
        List<SimpleGradeInfoVO> result = gradeInfoDao.getAll();
        log.info("GradeInfoService.getAll result: count={}", result.size());
        return result;
    }
}

package com.akkkka.admin.module.business.funcampus.collegeInfo.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import com.akkkka.admin.module.business.funcampus.collegeInfo.dao.CollegeInfoDao;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoAddForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 学院信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Service
@Slf4j
public class CollegeInfoService {

    @Resource
    private CollegeInfoDao collegeInfoDao;
    @Resource
    private BackendUserManager backendUserManager;

    /**
     * 分页查询
     */
    public PageResult<CollegeInfoVO> queryPage(CollegeInfoQueryForm queryForm) {
        log.info("CollegeInfoService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CollegeInfoVO> list = collegeInfoDao.queryPage(page, queryForm);
        log.info("CollegeInfoService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CollegeInfoAddForm addForm) {
        log.info("CollegeInfoService.add called, addForm={}", addForm);
        CollegeInfoEntity collegeInfoEntity = SmartBeanUtil.copy(addForm, CollegeInfoEntity.class);
        int result = collegeInfoDao.insert(collegeInfoEntity);
        if (result > 0) {
            log.info("CollegeInfoService.add success: new record created with id={}", collegeInfoEntity.getId());
        } else {
            log.error("CollegeInfoService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(CollegeInfoUpdateForm updateForm) {
        log.info("CollegeInfoService.update called, updateForm={}", updateForm);
        CollegeInfoEntity collegeInfoEntity = SmartBeanUtil.copy(updateForm, CollegeInfoEntity.class);
        int result = collegeInfoDao.updateById(collegeInfoEntity);
        if (result > 0) {
            log.info("CollegeInfoService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("CollegeInfoService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("CollegeInfoService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("CollegeInfoService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = collegeInfoDao.batchUpdateDeleted(idList, true);
        log.info("CollegeInfoService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("CollegeInfoService.delete called, id={}", id);
        if (null == id){
            log.warn("CollegeInfoService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = collegeInfoDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("CollegeInfoService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("CollegeInfoService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }

    /**
     * 根据用户id获取学院信息
     */
    public List<SimpleCollegeInfoVO> getCollegeInfoByUserId() {
        Long userId = SmartRequestUtil.getRequestUserId();
        log.info("CollegeInfoService.getCollegeInfoByUserId called, userId={}", userId);
        List<SimpleCollegeInfoVO> collegeInfoList = this.collegeInfoDao.getCollegeInfoByUserId(userId);
        if (CollectionUtils.isEmpty(collegeInfoList)){
            log.error("通过userid:{}获取的schoolid获取的collegeInfo为空", userId);
        } else {
            log.info("CollegeInfoService.getCollegeInfoByUserId result: count={}", collegeInfoList.size());
        }
        return collegeInfoList;
    }
}

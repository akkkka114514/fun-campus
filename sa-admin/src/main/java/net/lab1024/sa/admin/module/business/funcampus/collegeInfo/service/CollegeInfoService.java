package net.lab1024.sa.admin.module.business.funcampus.collegeInfo.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.dao.CollegeInfoDao;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
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
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CollegeInfoVO> list = collegeInfoDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CollegeInfoAddForm addForm) {
        CollegeInfoEntity collegeInfoEntity = SmartBeanUtil.copy(addForm, CollegeInfoEntity.class);
        collegeInfoDao.insert(collegeInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(CollegeInfoUpdateForm updateForm) {
        CollegeInfoEntity collegeInfoEntity = SmartBeanUtil.copy(updateForm, CollegeInfoEntity.class);
        collegeInfoDao.updateById(collegeInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        collegeInfoDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        collegeInfoDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    /**
     * 根据用户id获取学院信息
     */
    public List<SimpleCollegeInfoVO> getCollegeInfoByUserId() {
        Long userId = SmartRequestUtil.getRequestUserId();
        List<SimpleCollegeInfoVO> collegeInfoList = this.collegeInfoDao.getCollegeInfoByUserId(userId);
        if (CollectionUtils.isEmpty(collegeInfoList)){
            log.error("通过userid:{}获取的schoolid获取的collegeInfo为空", userId);
        }
        return collegeInfoList;
    }
}

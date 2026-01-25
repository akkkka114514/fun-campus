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
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 年级信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@Service
public class GradeInfoService {

    @Resource
    private GradeInfoDao gradeInfoDao;

    /**
     * 分页查询
     */
    public PageResult<GradeInfoVO> queryPage(GradeInfoQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<GradeInfoVO> list = gradeInfoDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(GradeInfoAddForm addForm) {
        GradeInfoEntity gradeInfoEntity = SmartBeanUtil.copy(addForm, GradeInfoEntity.class);
        gradeInfoDao.insert(gradeInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(GradeInfoUpdateForm updateForm) {
        GradeInfoEntity gradeInfoEntity = SmartBeanUtil.copy(updateForm, GradeInfoEntity.class);
        gradeInfoDao.updateById(gradeInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        gradeInfoDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        gradeInfoDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    /**
     * 获取所有
     */
    public List<SimpleGradeInfoVO> getAll() {
        return gradeInfoDao.getAll();
    }
}

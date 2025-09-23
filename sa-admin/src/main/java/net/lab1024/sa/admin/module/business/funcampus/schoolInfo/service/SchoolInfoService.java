package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.dao.SchoolInfoDao;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 学校信息表 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Service
public class SchoolInfoService {

    @Resource
    private SchoolInfoDao schoolInfoDao;

    /**
     * 分页查询
     */
    public PageResult<SchoolInfoVO> queryPage(SchoolInfoQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SchoolInfoVO> list = schoolInfoDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SchoolInfoAddForm addForm) {
        SchoolInfoEntity schoolInfoEntity = SmartBeanUtil.copy(addForm, SchoolInfoEntity.class);
        schoolInfoDao.insert(schoolInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(SchoolInfoUpdateForm updateForm) {
        SchoolInfoEntity schoolInfoEntity = SmartBeanUtil.copy(updateForm, SchoolInfoEntity.class);
        schoolInfoDao.updateById(schoolInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        schoolInfoDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        schoolInfoDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}

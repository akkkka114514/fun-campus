package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 学校信息表 Dao
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Mapper
public interface SchoolInfoDao extends BaseMapper<SchoolInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SchoolInfoVO> queryPage(Page page, @Param("queryForm") SchoolInfoQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}

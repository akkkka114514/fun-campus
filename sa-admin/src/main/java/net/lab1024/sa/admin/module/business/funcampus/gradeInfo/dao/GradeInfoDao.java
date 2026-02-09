package net.lab1024.sa.admin.module.business.funcampus.gradeInfo.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.entity.GradeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.GradeInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.SimpleGradeInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 年级信息 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@Mapper
public interface GradeInfoDao extends BaseMapper<GradeInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<GradeInfoVO> queryPage(Page page, @Param("queryForm") GradeInfoQueryForm queryForm);

    /**
     * 更新删除状态
     */
    int updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    int batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 获取全部
     */
    List<SimpleGradeInfoVO> getAll();

}

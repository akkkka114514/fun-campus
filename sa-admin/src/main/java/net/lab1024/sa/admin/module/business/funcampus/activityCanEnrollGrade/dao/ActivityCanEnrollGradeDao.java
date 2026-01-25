package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo.ActivityCanEnrollGradeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 活动能报名的年级 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCanEnrollGradeDao extends BaseMapper<ActivityCanEnrollGradeEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityCanEnrollGradeVO> queryPage(Page page, @Param("queryForm") ActivityCanEnrollGradeQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}

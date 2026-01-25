package net.lab1024.sa.admin.module.business.funcampus.activityCategory.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 活动分类 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCategoryDao extends BaseMapper<ActivityCategoryEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityCategoryVO> queryPage(Page page, @Param("queryForm") ActivityCategoryQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 获取所有类型
     */
    List<SimpleActivityCategoryVO> getAll();
}

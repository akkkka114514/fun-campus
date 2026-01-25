package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo.ActivityCanEnrollTribeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 活动能报名的部落 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCanEnrollTribeDao extends BaseMapper<ActivityCanEnrollTribeEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityCanEnrollTribeVO> queryPage(Page page, @Param("queryForm") ActivityCanEnrollTribeQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}

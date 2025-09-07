package net.lab1024.sa.admin.module.business.funcampus.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 活动管理 Dao
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityDao extends BaseMapper<ActivityEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityVO> queryPage(Page page, @Param("queryForm") ActivityQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}

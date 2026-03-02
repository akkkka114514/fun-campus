package net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.ActivitySigninManager.domain.vo.ActivitySigninManagerVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 活动签到管理员 Dao
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivitySigninManagerDao extends BaseMapper<ActivitySigninManagerEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivitySigninManagerVO> queryPage(Page page, @Param("queryForm") ActivitySigninManagerQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}

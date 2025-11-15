package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.entity.OrganizerCadreEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.vo.OrganizerCadreVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 组织干事用户 Dao
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Mapper
public interface OrganizerCadreDao extends BaseMapper<OrganizerCadreEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<OrganizerCadreVO> queryPage(Page page, @Param("queryForm") OrganizerCadreQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    /*
    * cadre是否能管理这个activity
    * 即查询cadre属于哪个organizer
    * 然后activity是否是这个organizer发布的
    */
    boolean canManageActivity(@Param("cadreId")Long cadreId,@Param("activityId")Long activityId);
}

package net.lab1024.sa.admin.module.business.funcampus.dao;

import java.time.LocalDateTime;
import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityScheduleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动时间表 Dao
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityScheduleDao extends BaseMapper<ActivityScheduleEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityScheduleVO> queryPage(Page page, @Param("queryForm") ActivityScheduleQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("activityId")Long activityId,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);
    
    /**
     * 根据活动ID列表查询时间表
     * 
     * @param activityIds 活动ID列表
     * @return 时间表列表
     */
    List<ActivityScheduleEntity> selectByActivityIds(@Param("activityIds") List<Long> activityIds);
    
    /**
     * 查询指定时间范围内有关键时间点的活动
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活动时间表列表
     */
    List<ActivityScheduleEntity> selectActivitiesWithKeyTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
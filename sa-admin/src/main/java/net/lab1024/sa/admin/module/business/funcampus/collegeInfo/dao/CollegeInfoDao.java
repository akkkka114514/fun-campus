package net.lab1024.sa.admin.module.business.funcampus.collegeInfo.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 学院信息 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Mapper
public interface CollegeInfoDao extends BaseMapper<CollegeInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CollegeInfoVO> queryPage(Page page, @Param("queryForm") CollegeInfoQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 根据userId获取用户所在学校所有学院
     */
    List<SimpleCollegeInfoVO> getCollegeInfoByUserId(@Param("userId")Long userId);
}

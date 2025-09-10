package net.lab1024.sa.admin.module.system.backendUser.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.BackendUserQueryForm;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 后台用户 Dao
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-08 20:47:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
@Component
public interface BackendUserDao extends BaseMapper<BackendUserEntity> {
    /**
     * 查询后台用户列表
     */
    List<BackendUserVO> queryBackendUser(Page page, @Param("queryForm") BackendUserQueryForm queryForm);

    /**
     * 查询后台用户
     */
    List<BackendUserVO> selectBackendUserByDisabledAndDeleted(@Param("disabledFlag") Boolean disabledFlag, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 通过登录名查询
     */
    BackendUserEntity getByUsername(@Param("username") String username, @Param("deletedFlag") Boolean deletedFlag);


    /**
     * 通过邮箱账号查询
     */
    BackendUserEntity getByEmail(@Param("email") String email, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取所有后台用户
     */
    List<BackendUserVO> listAll();


    /**
     * 获取一批后台用户
     */
    List<BackendUserVO> getBackendUserByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询单个后台用户信息
     */
    BackendUserVO getBackendUserById(@Param("backendUserId") Long id);


    /**
     * 获取所有
     */
    List<Long> getBackendUserId(@Param("leaveFlag") Boolean leaveFlag, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 后台用户重置密码
     */
    Integer updatePassword(@Param("backendUserId") Long backendUserId, @Param("password") String password);

    /*
     * 根据指定字段查询用户（排除指定ID）
     */
    BackendUserEntity getByFieldExcludingId(@Param("field") String field, @Param("value") String value,
                                            @Param("deletedFlag") boolean deletedFlag,@Param("excludeId") Long excludeId);

    /**
     * 更新单个
     */
    void updateDisableFlag(@Param("id") Long id, @Param("disabledFlag") Boolean disabledFlag);

}
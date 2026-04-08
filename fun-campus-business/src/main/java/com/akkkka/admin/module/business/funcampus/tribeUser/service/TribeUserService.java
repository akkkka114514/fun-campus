package com.akkkka.admin.module.business.funcampus.tribeUser.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.tribeUser.dao.TribeUserDao;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserAddForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserQueryForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.vo.TribeUserVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 参与部落的用户 Service
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@Service
public class TribeUserService {

    @Resource
    private TribeUserDao tribeUserDao;

    /**
     * 分页查询
     */
    public PageResult<TribeUserVO> queryPage(TribeUserQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<TribeUserVO> list = tribeUserDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(TribeUserAddForm addForm) {
        TribeUserEntity tribeUserEntity = SmartBeanUtil.copy(addForm, TribeUserEntity.class);
        tribeUserDao.insert(tribeUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(TribeUserUpdateForm updateForm) {
        TribeUserEntity tribeUserEntity = SmartBeanUtil.copy(updateForm, TribeUserEntity.class);
        tribeUserDao.updateById(tribeUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        tribeUserDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        tribeUserDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

/**
 * 检查用户是否存在于指定的部落中
 * @param userIds 用户ID列表
 * @param tribeIds 部落ID列表
 * @return 如果所有用户都至少存在于一个指定的部落中，则返回true；否则返回false
 */
    public boolean usersExistsInTribes(List<Long> userIds,List<Long> tribeIds){
    // 遍历每个用户ID
        for(Long userId:userIds){
        // 创建Lambda查询包装器
            LambdaQueryWrapper<TribeUserEntity> qw=new LambdaQueryWrapper<>();
        // 设置查询条件：查询指定用户的部落关联信息，只选择部落ID字段
            qw.eq(TribeUserEntity::getPortalUserId,userId)
                .eq(TribeUserEntity::getDeletedFlag,false)
                .select(TribeUserEntity::getTribeId);
        // 执行查询，获取用户参与的部落列表
            List<TribeUserEntity> list = tribeUserDao.selectList(qw);
        // 如果用户参与了至少一个部落
            if(list.isEmpty()){
                return false;
            }
            // 检查用户参与的部落是否都不在传入的tribeIds中
            //如果用户参与的tribe一个也没有在传入的tribeIds中出现，则返回false
            if(list.stream()
                    .map(TribeUserEntity::getTribeId)  // 提取每个部落关联对象的部落ID
                    .noneMatch(tribeIds::contains)){   // 检查是否有部落ID存在于传入的tribeIds中
                return false;  // 如果没有，则返回false
            }

        }
    // 如果所有用户都至少存在于一个指定的部落中，则返回true
        return true;
    }
}

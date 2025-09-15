package net.lab1024.sa.admin.module.system.datascope.service;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import net.lab1024.sa.admin.module.system.backendUser.dao.BackendUserDao;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.role.dao.RoleDataScopeDao;
import net.lab1024.sa.admin.module.system.role.dao.RoleBackendUserDao;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleDataScopeEntity;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据范围
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class DataScopeViewService {

    @Resource
    private RoleBackendUserDao roleBackendUserDao;

    @Resource
    private RoleDataScopeDao roleDataScopeDao;

    @Resource
    private BackendUserDao backendUserDao;

    /**
     * 根据后台用户id 获取各数据范围最大的可见范围 map<dataScopeType,viewType></>
     */
    public DataScopeViewTypeEnum getBackendUserDataScopeViewType(DataScopeTypeEnum dataScopeTypeEnum, Long id) {
        BackendUserEntity backendUserEntity = backendUserDao.selectById(id);
        if (backendUserEntity == null || backendUserEntity.getId() == null) {
            return DataScopeViewTypeEnum.ME;
        }

        List<Long> roleIdList = roleBackendUserDao.selectRoleIdByBackendUserId(id);
        //未设置角色 默认本人
        if (CollectionUtils.isEmpty(roleIdList)) {
            return DataScopeViewTypeEnum.ME;
        }
        //未设置角色数据范围 默认本人
        List<RoleDataScopeEntity> dataScopeRoleList = roleDataScopeDao.listByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(dataScopeRoleList)) {
            return DataScopeViewTypeEnum.ME;
        }
        Map<Integer, List<RoleDataScopeEntity>> listMap = dataScopeRoleList.stream().collect(Collectors.groupingBy(RoleDataScopeEntity::getDataScopeType));
        List<RoleDataScopeEntity> viewLevelList = listMap.getOrDefault(dataScopeTypeEnum.getValue(), Lists.newArrayList());
        if (CollectionUtils.isEmpty(viewLevelList)) {
            return DataScopeViewTypeEnum.ME;
        }
        RoleDataScopeEntity maxLevel = viewLevelList.stream().max(Comparator.comparing(e -> SmartEnumUtil.getEnumByValue(e.getViewType(), DataScopeViewTypeEnum.class).getLevel())).get();
        return SmartEnumUtil.getEnumByValue(maxLevel.getViewType(), DataScopeViewTypeEnum.class);
    }

    /**
     * 获取本人相关 可查看后台用户id
     */
    private List<Long> getMeBackendUserIdList(Long backendUserId) {
        return Lists.newArrayList(backendUserId);
    }
    /**
     * 获取某人可以查看的所有人员数据
     */
    public List<Long> getCanViewBackendUserId(DataScopeViewTypeEnum viewType, Long backendUserId) {
        if (DataScopeViewTypeEnum.ME == viewType) {
            return this.getMeBackendUserIdList(backendUserId);
        }
        // 可以查看所有后台用户数据
        return Lists.newArrayList();
    }
}

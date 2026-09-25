package com.akkkka.module.support.reload;

import jakarta.annotation.Resource;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.module.support.reload.core.AbstractSmartReloadCommand;
import com.akkkka.module.support.reload.core.domain.SmartReloadItem;
import com.akkkka.module.support.reload.core.domain.SmartReloadResult;
import com.akkkka.module.support.reload.dao.ReloadItemDao;
import com.akkkka.module.support.reload.dao.ReloadResultDao;
import com.akkkka.module.support.reload.domain.ReloadItemEntity;
import com.akkkka.module.support.reload.domain.ReloadResultEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * reload 操作
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Component
public class ReloadCommand extends AbstractSmartReloadCommand {

    @Resource
    private ReloadItemDao reloadItemDao;

    @Resource
    private ReloadResultDao reloadResultDao;

    /**
     * 读取数据库中SmartReload项
     *
     * @return List<ReloadItem>
     */
    @Override
    public List<SmartReloadItem> readReloadItem() {
        List<ReloadItemEntity> reloadItemEntityList = reloadItemDao.selectList(null);
        return SmartBeanUtil.copyList(reloadItemEntityList, SmartReloadItem.class);
    }


    /**
     * 保存reload结果
     *
     * @param smartReloadResult
     */
    @Override
    public void handleReloadResult(SmartReloadResult smartReloadResult) {
        ReloadResultEntity reloadResultEntity = SmartBeanUtil.copy(smartReloadResult, ReloadResultEntity.class);
        reloadResultDao.insert(reloadResultEntity);
    }
}

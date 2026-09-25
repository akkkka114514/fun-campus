package com.akkkka.module.support.reload.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.akkkka.module.support.reload.domain.ReloadItemEntity;
import com.akkkka.module.support.reload.domain.ReloadItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * t_reload_item 数据表dao
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface ReloadItemDao extends BaseMapper<ReloadItemEntity> {

    List<ReloadItemVO> query();
}

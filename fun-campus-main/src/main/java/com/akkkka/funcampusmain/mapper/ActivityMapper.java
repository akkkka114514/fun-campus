package com.akkkka.funcampusmain.mapper;

import com.akkkka.funcampusmainmodel.entity.Activity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface ActivityMapper extends BaseMapper<Activity> {
    List<Activity> listByUserId(Integer userId);

    Page<Activity> page(Integer pageNum, Integer pageSize);

    Page<Activity> pageBySchool(Page<Activity> page, Integer schoolId);
}

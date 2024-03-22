package com.akkkka.funcampusmain.controller;

import com.akkkka.funcampusmainmodel.entity.Activity;
import com.akkkka.funcampusmainmodel.entity.scope.ScopeInsert;
import com.akkkka.funcampusmainmodel.entity.scope.ScopeUpdate;
import com.akkkka.funcampusmainapi.api.IActivityService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private IActivityService activityService;

    @RequestMapping("/create")
    @Validated(ScopeInsert.class)
    public CommonResponse<Void> create(@RequestBody @Valid Activity activity){
        activityService.addActivity(activity);
        return CommonResponse.success();
    }

    /**
     * update activity info by id
     * */
    @RequestMapping("/update")
    @Validated(ScopeUpdate.class)
    public CommonResponse<Void> update(@RequestBody @Valid Activity activity){
        activityService.updateActivity(activity);
        return CommonResponse.success();
    }

    /**
     * of course, delete activity logically
     * */
    @RequestMapping("/delete")
    public CommonResponse<Void> delete(@RequestParam("id") Integer id){
        if(id == null || id < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.deleteActivity(id);
        return CommonResponse.success();
    }

    @RequestMapping("/get")
    public CommonResponse<Activity> get(@RequestParam("id") Integer id){
        if(id == null || id < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        Activity activity = activityService.getById(id);
        if(activity == null){
            return CommonResponse.fail(ResponseEnum.NO_SUCH_RECORD_IN_DB);
        }
        return CommonResponse.success(activity);
    }

//    @RequestMapping("/list")
//    public CommonResponse<Page<Activity>> list(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
//        if( page == null || page < 0 || size == null || size < 0){
//            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
//        }
//        Page<Activity> pageList = activityService.page(new Page<>(page, size));
//        return CommonResponse.success(pageList);
//    }

    @RequestMapping("/listByUser")
    public CommonResponse<List<Activity>> listByUserId(@RequestParam("userId") Integer userId){
        if(userId == null || userId < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        List<Activity> list = activityService.listByUserId(userId);
        return CommonResponse.success(list);
    }
    @RequestMapping("/enroll")
    public CommonResponse<Void> enroll(@RequestParam("userId") Integer userId, @RequestParam("activityId") Integer activityId) {
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.enroll(userId, activityId);
        return CommonResponse.success();
    }

    @RequestMapping("/sign-in")
    public CommonResponse<Void> signIn(@RequestParam Integer userId, @RequestParam Integer activityId){
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.signIn(activityId, userId);
        return CommonResponse.success();
    }

    @RequestMapping("/page")
    public CommonResponse<IPage<Activity>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize){
        if(pageNum == null || pageNum < 0 || pageSize == null || pageSize < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        IPage<Activity> page = new Page<>(pageNum, pageSize);
        page = activityService.page(page);
        return CommonResponse.success(page);
    }
    @RequestMapping("/pageBySchool")
    public CommonResponse<IPage<Activity>> pageBySchool(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam Integer schoolId){
        if(pageNum == null || pageNum < 0 || pageSize == null || pageSize < 0 || schoolId == null || schoolId < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return CommonResponse.success(activityService.pageBySchoolId(pageNum, pageSize, schoolId));
    }

    @RequestMapping("/cancelEnroll")
    public CommonResponse<Void> cancelEnroll(@RequestParam Integer userId, @RequestParam Integer activityId){
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.cancelEnroll(userId, activityId);
        return CommonResponse.success();
    }
}

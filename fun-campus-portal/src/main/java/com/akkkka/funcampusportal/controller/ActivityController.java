package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.Activity;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.domain.scope.ScopeUpdate;
import com.akkkka.funcampusportal.service.IActivityService;
import com.akkkka.common.core.domain.R;
import com.akkkka.funcampusutil.constant.ResponseEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "activity")
public class ActivityController {

    @Resource
    private IActivityService activityService;

    @RequestMapping("/create")
    @Validated(ScopeInsert.class)
    @ApiOperation(value = "create activity")
    public R<Void> create(@RequestBody @Valid Activity activity){
        activityService.add(activity);
        return R.ok();
    }

    /**
     * update activity info by id
     * */
    @RequestMapping("/update")
    @Validated(ScopeUpdate.class)
    @ApiOperation(value = "update activity")
    public R<Void> update(@RequestBody @Valid Activity activity){
        activityService.update(activity);
        return R.ok();
    }

    /**
     * of course, delete activity logically
     * */
    @RequestMapping("/delete")
    @ApiOperation(value = "delete activity")
    public R<Void> delete(@RequestParam("id") Integer id){
        if(id == null || id < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.delete(id);
        return R.ok();
    }

    @RequestMapping("/get")
    @ApiOperation(value = "get activity")
    public R<Activity> get(@RequestParam("id") Integer id){
        if(id == null || id < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        Activity activity = activityService.getById(id);
        if(activity == null){
            return R.fail(ResponseEnum.NO_SUCH_RECORD_IN_DB);
        }
        return R.ok(activity);
    }

//    @RequestMapping("/list")
//    public R<Page<Activity>> list(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
//        if( page == null || page < 0 || size == null || size < 0){
//            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
//        }
//        Page<Activity> pageList = activityService.page(new Page<>(page, size));
//        return R.ok(pageList);
//    }

    @RequestMapping("/listByUser")
    @ApiOperation(value = "list activity by user id")
    public R<List<Activity>> listByUserId(@RequestParam("userId") Integer userId){
        if(userId == null || userId < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        List<Activity> list = activityService.listByUserId(userId);
        return R.ok(list);
    }
    @RequestMapping("/enroll")
    @ApiOperation(value = "enroll activity")
    public R<Void> enroll(@RequestParam("userId") Integer userId, @RequestParam("activityId") Integer activityId) {
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.enroll(userId, activityId);
        return R.ok();
    }

    @RequestMapping("/sign-in")
    @ApiOperation(value = "sign in activity")
    public R<Void> signIn(@RequestParam Integer userId, @RequestParam Integer activityId){
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.signIn(activityId, userId);
        return R.ok();
    }

    @RequestMapping("/page")
    @ApiOperation(value = "page activity")
    public R<IPage<Activity>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize){
        if(pageNum == null || pageNum < 0 || pageSize == null || pageSize < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        IPage<Activity> page = new Page<>(pageNum, pageSize);
        page = activityService.page(page);
        return R.ok(page);
    }
    @RequestMapping("/pageBySchool")
    @ApiOperation(value = "page activity by school id")
    public R<IPage<Activity>> pageBySchool(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam Integer schoolId){
        if(pageNum == null || pageNum < 0 || pageSize == null || pageSize < 0 || schoolId == null || schoolId < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return R.ok(activityService.pageBySchoolId(pageNum, pageSize, schoolId));
    }

    @RequestMapping("/cancelEnroll")
    @ApiOperation(value = "cancel enroll activity")
    public R<Void> cancelEnroll(@RequestParam Integer userId, @RequestParam Integer activityId){
        if(userId == null || userId < 0 || activityId == null || activityId < 0){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        activityService.cancelEnroll(userId, activityId);
        return R.ok();
    }
}

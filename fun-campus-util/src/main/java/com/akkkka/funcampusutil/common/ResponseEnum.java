package com.akkkka.funcampusutil.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author akkkka
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    SUCCESS("00000","成功"),
    PARAM_NOT_VALIDATE("10000","Controller层字段校验异常"),
    NO_SUCH_USER("10001","根据字段查不到该用户"),
    NOT_FOUND_IN_REDIS("10002","所查找的redis中的对象已过期或不存在"),
    SMS_CODE_WRONG("10003","验证码错误"),
    EXISTS_IN_DB("10004","数据库中已存在该记录"),
    NO_SUCH_RECORD_IN_DB("10005","数据库中不存在该记录"),
    NOT_ENROLL_TIME("10006","还不能报名或报名已结束"),
    GOT_ENROLL_NUM_MAX("10007","已达到报名人数上限"),
    NOT_IN_THE_SCHOOL_OR_COLLEGE("10008","该用户不在该学校或该学院"),
    RECORD_DELETED_LOGICALLY("10009","该记录已删除，不可操作"),
    RECORD_IS_DELETED_IN_DB("10010","数据库中已删除该记录"),
    NOT_CORRESPOND_TO_RECORD_IN_DB("10011","传入的数据与数据库中记录不对应"),
    ALREADY_SIGN_IN("10012","该用户已在该活动签到"),
    NOT_SIGN_IN("10013","该用户未在该活动签到"),
    AUTHENTICATION_FAILED("10014","认证失败"),
    NOT_LOGIN_OR_TOKEN_EXPIRED("10015","未登录或登录已过期"),
    PASSWORD_WRONG("10015","密码错误"),
    ACTIVITY_NOT_IN_CORRECT_STATUS("10016","活动状态不正确"),
    OPERATION_DUPLICATED("10017","操作重复"),
    PERMISSION_DENIED("10018","操作失败，没有该操作权限");

    private final String code;
    private final String message;
}

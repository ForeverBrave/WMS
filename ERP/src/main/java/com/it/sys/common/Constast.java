package com.it.sys.common;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 16:17
 */
public class Constast {

    /**
     * 状态码
     */
    public static final Integer OK = 200;
    public static final Integer ERROR_PASS = -1;
    public static final Integer ERROR_CODE = -2;

    /**
     * 菜单权限类型
     */
    public static final String TYPE_MNEU = "menu";
    public static final String TYPE_PERMISSION = "permission";

    /**
     * 可用状态    1
     * 不可用状态  0
     */
    public static final Object AVAILABLE_TRUE = 1;
    public static final Object AVAILABLE_FALSE = 0;

    /**
     * 用户类型
     * 超级管理员  0
     * 普通用户    1
     */
    public static final Integer USER_TYPE_SUPER = 0;
    public static final Integer USER_TYPE_NORMAL = 1;

    /**
     * 展开类型
     */
    public static final Integer OPEN_TRUE = 1;
    public static final Integer OPEN_FALSE = 0;

}

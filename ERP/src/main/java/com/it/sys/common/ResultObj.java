package com.it.sys.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    public static final ResultObj LOGIN_SUCCESS = new ResultObj(Constast.OK,"登录成功！");
    public static final ResultObj LOGIN_ERROR_PASS = new ResultObj(Constast.ERROR_PASS,"登录失败，用户名或密码不正确！");
    public static final ResultObj LOGIN_ERROR_CODE = new ResultObj(Constast.ERROR_CODE,"登录失败，验证码不正确！");


    private Integer code;
    private String msg;
}

package com.it.sys.controller;

import com.it.sys.common.ActiverUser;
import com.it.sys.common.ResultObj;
import com.it.sys.common.WebUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录前端控制器
 *
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 14:01
 */
@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping("login")
    public ResultObj login(String loginname, String pwd){
        //创建一个subject
        Subject subject = SecurityUtils.getSubject();
        //将用户名和密码进行封装
        AuthenticationToken token = new UsernamePasswordToken(loginname,pwd);
        //调用login方法
        try {
            subject.login(token);
            ActiverUser activerUser = (ActiverUser) subject.getPrincipal();
            WebUtils.getSession().setAttribute("user",activerUser.getUser());
            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR_PASS;
        }
    }
}

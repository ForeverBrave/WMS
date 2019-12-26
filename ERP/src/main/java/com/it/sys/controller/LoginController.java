package com.it.sys.controller;

import com.it.sys.common.ActiverUser;
import com.it.sys.common.ResultObj;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.Loginfo;
import com.it.sys.service.LoginfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @Autowired
    private LoginfoService loginfoService;

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

            //记录登录日志
            Loginfo loginfo = new Loginfo();
            loginfo.setLoginname(activerUser.getUser().getName()+"-"+activerUser.getUser().getLoginname());
            loginfo.setLoginip(WebUtils.getRequest().getRemoteAddr());
            loginfo.setLogintime(new Date());
            loginfoService.save(loginfo);

            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR_PASS;
        }
    }
}

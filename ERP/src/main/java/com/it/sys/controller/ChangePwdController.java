package com.it.sys.controller;

import com.it.sys.common.ResultObj;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.User;
import com.it.sys.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/23 13:24
 */
@RestController
@RequestMapping("password")
public class ChangePwdController {

    @Autowired
    private UserService userService;

    /**
     * 修改密码
     * @param oldpwd
     * @param newpwd
     * @return
     */
    @RequestMapping("updatePwd")
    public ResultObj updatePwd(@Param("oldpwd") String oldpwd,@Param("newpwd") String newpwd){

        User user = (User) WebUtils.getSession().getAttribute("user");
        String loginname = user.getLoginname();
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = new UsernamePasswordToken(loginname, oldpwd);
        try {
            subject.login(token);
            this.userService.updatePwd();

            return ResultObj.CHANGE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CHANGE_ERROR;
        }
    }

}

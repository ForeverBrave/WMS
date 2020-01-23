package com.it.sys.controller;

import cn.hutool.core.util.IdUtil;
import com.it.sys.common.ResultObj;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.User;
import com.it.sys.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

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
            //加盐
            String salt = IdUtil.simpleUUID().toUpperCase();
            user.setSalt(salt);
            //加盐加密并散列2次
            user.setPwd(new Md5Hash(newpwd,salt,2).toString());
            this.userService.updateById(user);
            return ResultObj.CHANGE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CHANGE_ERROR;
        }
    }

    public void a(){
        HttpSession session = WebUtils.getSession();
        session.invalidate();
    }

}

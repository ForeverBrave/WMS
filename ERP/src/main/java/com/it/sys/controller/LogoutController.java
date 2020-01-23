package com.it.sys.controller;

import com.it.sys.common.WebUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/23 16:17
 */
@RestController
@RequestMapping("logout")
public class LogoutController {

    /**
     * 退出并跳转到登录页面
     * @return
     */
    @RequestMapping("logout")
    public ModelAndView logout(){
        HttpSession session = WebUtils.getSession();
        session.removeAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/sys/toLogin");
        return modelAndView;
    }
}

package com.it.sys.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 14:17
 */
public class WebUtils {
    /**
     *得到request
     */
    public static HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request;
    }

    /**
     * 得到session
     * @return
     */
    public static HttpSession getSession(){
        return getRequest().getSession();
    }
}

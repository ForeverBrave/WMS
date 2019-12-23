package com.it.sys.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.sys.common.ActiverUser;
import com.it.sys.domain.User;
import com.it.sys.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 12:48
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public String getName(){
        //获取底层类简称
        return this.getClass().getSimpleName();
    }

    /**
     * 做认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //new 查询构建器
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //登录名为传来的token中的身份信息
        userQueryWrapper.eq("loginname",token.getPrincipal().toString());
        //根据查询条件查询用户
        User user = userService.getOne(userQueryWrapper);
        if(null!=user){
            ActiverUser activerUser = new ActiverUser();
            activerUser.setUser(user);
            //获取盐   转为ByteSource类型
            ByteSource salt = ByteSource.Util.bytes(user.getSalt());
            //因为需要返回AuthenticationInfo类型，所以使用他的实现类SimpleAuthenticationInfo
            //参数说明:1、任意类型（一般传User、Roles、Permission的包装类） 2、密码 3、盐 4、类名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activerUser,user.getPwd(),salt,this.getName());

            return info;
        }
        return null;
    }
    /**
     * 做授权
     * @param principal
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        return null;
    }


}

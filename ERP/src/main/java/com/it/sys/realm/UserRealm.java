package com.it.sys.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.sys.common.ActiverUser;
import com.it.sys.common.Constast;
import com.it.sys.domain.Permission;
import com.it.sys.domain.User;
import com.it.sys.service.PermissionService;
import com.it.sys.service.RoleService;
import com.it.sys.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 12:48
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy   //懒加载(只有使用的时候才会加载)
    private UserService userService;

    @Autowired
    @Lazy
    private PermissionService permissionService;

    @Autowired
    @Lazy
    private RoleService roleService;

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

            //根据用户id查询percode
            //查询所有菜单
            QueryWrapper<Permission> wrapper = new QueryWrapper<>();
            //设置只能查询菜单
            wrapper.eq("type", Constast.TYPE_PERMISSION);
            wrapper.eq("available",Constast.AVAILABLE_TRUE);

            //普通用户(根据用户ID+角色+权限去查询)
            Integer userId = user.getId();
            //根据用户id查询角色
            List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(userId);
            //根据角色id取到权限和菜单id(pid不能重复)
            Set<Integer> pids = new HashSet<>();
            for (Integer rid : currentUserRoleIds) {
                List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
                pids.addAll(permissionIds);
            }
            List<Permission> list = new ArrayList<>();
            //根据角色id查询权限
            if (pids.size()>0){
                wrapper.in("id",pids);
                list = permissionService.list(wrapper);
            }
            List<String> percode = new ArrayList<>();
            for (Permission permission : list) {
                percode.add(permission.getPercode());
            }
            //放到
            activerUser.setPermissions(percode);

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
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        ActiverUser activerUser = (ActiverUser) principal.getPrimaryPrincipal();
        User user = activerUser.getUser();
        List<String> permissions = activerUser.getPermissions();
        if(user.getType()==Constast.USER_TYPE_SUPER){
            authorizationInfo.addStringPermission("*:*");
        }else {
            if(null!=permissions&&permissions.size()>0){
                authorizationInfo.addStringPermissions(permissions);
            }
        }
        return authorizationInfo;
    }


}

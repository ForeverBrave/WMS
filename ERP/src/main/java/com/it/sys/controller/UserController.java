package com.it.sys.controller;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.sys.common.Constast;
import com.it.sys.common.DataGridView;
import com.it.sys.common.PinyinUtils;
import com.it.sys.common.ResultObj;
import com.it.sys.domain.Dept;
import com.it.sys.domain.Role;
import com.it.sys.domain.User;
import com.it.sys.service.DeptService;
import com.it.sys.service.RoleService;
import com.it.sys.service.UserService;
import com.it.sys.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Brave
 * @since 2019-12-23
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;

    @RequestMapping("loadAllUser")
    public DataGridView loadAllUser(UserVo userVo){
        IPage<User> page = new Page<>(userVo.getPage(),userVo.getLimit());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(userVo.getName()), "loginname", userVo.getName()).or()
                .eq(StringUtils.isNotBlank(userVo.getName()), "name", userVo.getName());
        wrapper.eq(StringUtils.isNotBlank(userVo.getAddress()),"address",userVo.getAddress());
        wrapper.eq("type", Constast.USER_TYPE_NORMAL);
        wrapper.eq(userVo.getDeptid()!=null,"deptid",userVo.getDeptid());
        this.userService.page(page,wrapper);

        List<User> list = page.getRecords();

        for (User user : list) {
            //得到部门id，通过部门id从缓存中获取部门名，缓存中没有则从数据库获取
            Integer deptid = user.getDeptid();
            if(deptid!=null){
                Dept one = this.deptService.getById(deptid);
                user.setDeptname(one.getTitle());
            }
            //得到领导id，通过领导id从缓存中获取领导名，缓存中没有则从数据库获取
            Integer mgr = user.getMgr();
            if(mgr!=null){
                User one = this.userService.getById(mgr);
                user.setLeadername(one.getName());
            }
        }
        return new DataGridView(page.getTotal(),list);
    }

    /**
     * 得到最大的排序码
     * @return
     */
    @RequestMapping("loadUserMaxOrderNum")
    public Map<String,Object> loadUserMaxOrderNum(){
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ordernum");
        IPage<User> page = new Page<>(1,1);
        List<User> users = this.userService.page(page,wrapper).getRecords();
        if(users.size()>0){
            map.put("value",users.get(0).getOrdernum()+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     * 根据部门id查询用户
     * @return
     */
    @RequestMapping("loadUsersByDeptId")
    public DataGridView loadUsersByDeptId(Integer deptid){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(deptid!=null,"deptid",deptid);
        wrapper.eq("available",Constast.AVAILABLE_TRUE);
        wrapper.eq("type",Constast.USER_TYPE_NORMAL);
        List<User> list = this.userService.list(wrapper);
        return new DataGridView(list);
    }

    /**
     * 把用户名转为拼音
     * @param username
     * @return
     */
    @RequestMapping("changeChineseToPinyin")
    public Map<String,Object> changeChineseToPinyin(String username){
        Map<String, Object> map = new HashMap<>();
        if(null!=username){
            map.put("value", PinyinUtils.getPingYin(username));
        }else {
            map.put("value","");
        }
        return map;
    }

    /**
     * 添加用户
     * @param userVo
     * @return
     */
    @RequestMapping("addUser")
    public ResultObj addUser(UserVo userVo){
        try {
            //设置类型
            userVo.setType(Constast.USER_TYPE_NORMAL);
            userVo.setHiredate(new Date());
            //加盐
            String salt = IdUtil.simpleUUID().toUpperCase();
            userVo.setSalt(salt);
            //加盐加密并散列2次
            userVo.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD,salt,2).toString());
            this.userService.save(userVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 根据用户ID查询一个用户
     */
    @RequestMapping("loadUserById")
    public DataGridView loadUserById(Integer id) {
        return new DataGridView(this.userService.getById(id));
    }

    /**
     * 修改用户
     */
    @RequestMapping("updateUser")
    public ResultObj updateUser(UserVo userVo) {
        try {
            this.userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除用户
     */
    @RequestMapping("deleteUser")
    public ResultObj deleteUser(Integer id) {
        try {
            this.userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 重置用户密码
     */
    @RequestMapping("resetPwd")
    public ResultObj resetPwd(Integer id) {
        try {
            User user = new User();
            user.setId(id);
            //加盐
            String salt = IdUtil.simpleUUID().toUpperCase();
            user.setSalt(salt);
            //加盐加密并散列2次
            user.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD,salt,2).toString());
            this.userService.updateById(user);
            return ResultObj.RESET_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.RESET_ERROR;
        }
    }

    /**
     * 根据用户id查询角色并选中已拥有的角色
     * @param id
     * @return
     */
    @RequestMapping("initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id){
        //1、查询所有可用角色
        QueryWrapper<Role> wapper = new QueryWrapper<>();
        wapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Map<String, Object>> listMaps = this.roleService.listMaps(wapper);
        //2、查询当前用户拥有的角色id集合
        List<Integer> currentUserRoleIds = this.roleService.queryUserRoleIdsByUid(id);
        for (Map<String, Object> map : listMaps) {
            Boolean LAY_CHECKED = false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : currentUserRoleIds) {
                if(rid.equals(roleId)){
                    LAY_CHECKED = true;
                    break;
                }
            }
            map.put("LAY_CHECKED",LAY_CHECKED);
        }
        return new DataGridView((long) listMaps.size(),listMaps);
    }

    /**
     * 保存用户和角色之间的关系
     * @param uid
     * @param ids
     * @return
     */
    @RequestMapping("saveUserRole")
    public ResultObj saveUserRole(Integer uid,Integer[] ids){
        try {
            this.userService.saveUserRole(uid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}


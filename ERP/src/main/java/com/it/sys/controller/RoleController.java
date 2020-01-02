package com.it.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.sys.common.Constast;
import com.it.sys.common.DataGridView;
import com.it.sys.common.ResultObj;
import com.it.sys.common.TreeNode;
import com.it.sys.domain.Permission;
import com.it.sys.domain.Role;
import com.it.sys.service.PermissionService;
import com.it.sys.service.RoleService;
import com.it.sys.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Brave
 * @since 2020-01-02
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllRole")
    public DataGridView loadAllRole(RoleVo roleVo){
        IPage<Role> rolePage = new Page<>(roleVo.getPage(),roleVo.getLimit());
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(roleVo.getName()),"name",roleVo.getName());
        wrapper.like(StringUtils.isNotBlank(roleVo.getRemark()),"remark",roleVo.getRemark());
        //大于开始时间
        wrapper.eq(roleVo.getAvailable()!=null,"available",roleVo.getAvailable());
        wrapper.orderByDesc("createtime");
        this.roleService.page(rolePage,wrapper);
        return new DataGridView(rolePage.getTotal(),rolePage.getRecords());
    }

    /**
     * 添加
     * @param roleVo
     * @return
     */
    @RequestMapping("addRole")
    public ResultObj addRole(RoleVo roleVo){
        try {
            roleVo.setCreatetime(new Date());
            this.roleService.save(roleVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 更新
     * @param roleVo
     * @return
     */
    @RequestMapping("updateRole")
    public ResultObj updateRole(RoleVo roleVo){
        try {
            this.roleService.updateById(roleVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 按id删除
     * @return
     */
    @RequestMapping("deleteRole")
    public ResultObj deleteRole(Integer id){
        try {
            this.roleService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 根据角色ID加载菜单和权限树的json串
     * @param roleId
     * @return
     */
    @RequestMapping("initPermissionByRoleId")
    public DataGridView initPermissionByRoleId(Integer roleId){
        //查询所有可用的菜单和权限
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Permission> allPermissions = permissionService.list(wrapper);
        /*
         * 1、根据角色ID查询当前角色拥有的所有的权限或菜单ID
         * 2、根据查询出来的菜单ID查询权限和菜单数据
         */
        List<Integer> currentRolePermissions = this.roleService.queryRolePermissionIdsByRid(roleId);

        List<Permission> currentPermissions = null;
        //如果有ID就去查询
        if(currentRolePermissions.size()>0){
            wrapper.in("id",currentRolePermissions);
            currentPermissions = permissionService.list(wrapper);
        }else {
            currentPermissions = new ArrayList<>();
        }

        //构造List<TreeNode>
        List<TreeNode> nodes = new ArrayList<>();
        for (Permission p1 : allPermissions) {
            String checkArr="0";
            for (Permission p2 : currentPermissions) {
                if (p1.getId()==p2.getId()){
                    checkArr="1";
                    break;
                }
            }
            Boolean spread=(p1.getOpen()==null||p1.getOpen()==1)?true:false;
            nodes.add(new TreeNode(p1.getId(),p1.getPid(),p1.getTitle(),spread,checkArr));
        }
        return new DataGridView(nodes);
    }

    /**
     * 保存角色和菜单权限之间的关系
     * @return
     */
    @RequestMapping("saveRolePermission")
    public ResultObj saveRolePermission(Integer rid,Integer[] ids){

        try {
            this.roleService.saveRolePermission(rid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }

    }

}


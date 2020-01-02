package com.it.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.sys.common.*;
import com.it.sys.domain.Dept;
import com.it.sys.domain.Permission;
import com.it.sys.domain.User;
import com.it.sys.service.PermissionService;
import com.it.sys.vo.DeptVo;
import com.it.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 菜单管理前端控制器
 *
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 15:26
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("loadIndexLeftMenuJson")
    public DataGridView loadIndexLeftMenuJson(PermissionVo permissionVo){
        //查询所有菜单
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        //设置只能查询菜单
        queryWrapper.eq("type", Constast.TYPE_MNEU);
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);

        User user = (User) WebUtils.getSession().getAttribute("user");
        List<Permission> list = null;

        if(user.getType()==Constast.USER_TYPE_SUPER){
            //超级管理员
            list = permissionService.list(queryWrapper);
        }else {
            //普通用户(根据用户ID+角色+权限去查询)
            list = permissionService.list(queryWrapper);
        }

        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission p : list) {
            Integer id = p.getId();
            Integer pid = p.getPid();
            String title = p.getTitle();
            String icon = p.getIcon();
            String href = p.getHref();
            Boolean spread = p.getOpen()==Constast.OPEN_TRUE?true:false;
            treeNodes.add(new TreeNode(id,pid,title,icon,href,spread));
        }
        //构建层级关系
        List<TreeNode> nodeList = TreeNodeBuilder.build(treeNodes, 1);
        return new DataGridView(nodeList);
    }

    /*-------------------------------菜单管理开始-------------------------------*/

    /**
     * 加载菜单管理左边的菜单树json
     * @return
     */
    @RequestMapping("loadMenuManagerLeftTreeJson")
    public DataGridView loadMenuManagerLeftTreeJson(PermissionVo permissionVo){
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        //只查询菜单
        wrapper.eq("type",Constast.TYPE_MNEU);
        List<Permission> list = this.permissionService.list(wrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission permission : list) {
            Boolean spread = permission.getOpen()== 1 ? true : false;
            treeNodes.add(new TreeNode(permission.getId(),permission.getPid(),permission.getTitle(),spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 查询菜单
     * @return
     */
    @RequestMapping("loadAllMenu")
    public DataGridView loadAllMenu(PermissionVo permissionVo){
        IPage<Permission> noticePage = new Page<>(permissionVo.getPage(),permissionVo.getLimit());
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        //只查询菜单
        wrapper.eq(permissionVo.getId()!=null,"id",permissionVo.getId()).or().eq(permissionVo.getId()!=null,"pid",permissionVo.getId());
        wrapper.eq("type",Constast.TYPE_MNEU);
        wrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()),"title",permissionVo.getTitle());
        wrapper.orderByAsc("ordernum");
        this.permissionService.page(noticePage,wrapper);
        return new DataGridView(noticePage.getTotal(),noticePage.getRecords());
    }

    /**
     * 得到最大的排序码
     * @return
     */
    @RequestMapping("loadMenuMaxOrderNum")
    public Map<String,Object> loadMenuMaxOrderNum(){
        Map<String, Object> map = new HashMap<>();

        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ordernum");
        IPage<Permission> page = new Page<>(1,1);
        List<Permission> list = this.permissionService.page(page,wrapper).getRecords();
        if(list.size()>0){
            map.put("value",list.get(0).getOrdernum()+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     * 添加菜单
     *
     * @param permissionVo
     * @return
     */
    @RequestMapping("addMenu")
    public ResultObj addMenu(PermissionVo permissionVo){
        try {
            //设置添加类型
            permissionVo.setType(Constast.TYPE_MNEU);
            this.permissionService.save(permissionVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改菜单
     * @param permissionVo
     * @return
     */
    @RequestMapping("updateMenu")
    public ResultObj updateMenu(PermissionVo permissionVo){
        try {
            this.permissionService.updateById(permissionVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 查询当前ID的菜单有没有子菜单（根据前台的id，比对pid）
     * @param permissionVo
     * @return
     */
    @RequestMapping("checkMenuHasChildrenNode")
    public Map<String,Object> checkMenuHasChildrenNode(PermissionVo permissionVo){
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("pid",permissionVo.getId());
        List<Permission> menus = this.permissionService.list(wrapper);
        if (menus.size()>0){
            map.put("value",true);
        }else {
            map.put("value",false);
        }
        return map;
    }

    /**
     * 删除菜单
     * @param permissionVo
     * @return
     */
    @RequestMapping("deleteMenu")
    public ResultObj deleteMenu(PermissionVo permissionVo){
        try {
            this.permissionService.removeById(permissionVo.getId());
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /*-------------------------------菜单管理结束-------------------------------*/

}

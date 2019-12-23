package com.it.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.sys.common.*;
import com.it.sys.domain.Permission;
import com.it.sys.domain.User;
import com.it.sys.service.PermissionService;
import com.it.sys.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

}

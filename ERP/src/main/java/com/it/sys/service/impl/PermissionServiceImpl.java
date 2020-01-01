package com.it.sys.service.impl;

import com.it.sys.domain.Permission;
import com.it.sys.mapper.PermissionMapper;
import com.it.sys.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Brave
 * @since 2019-12-23
 */
@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public boolean removeById(Serializable id) {
        PermissionMapper permissionMapper = this.getBaseMapper();
        //先根据权限菜单ID删除权限表各个角色关系表里的数据
        permissionMapper.deleteRolePermissionByPid(id);
        //再调用原有方法删除sys_permission表的数据
        return super.removeById(id);
    }
}

package com.it.sys.service;

import com.it.sys.domain.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Brave
 * @since 2020-01-02
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据角色ID查询当前角色拥有的所有的权限或菜单ID
     * @param roleId
     * @return
     */
    List<Integer> queryRolePermissionIdsByRid(Integer roleId);

    /**
     * 保存角色和菜单权限之间的关系
     * @param roles
     * @param ids
     */
    void saveRolePermission(Integer roles, Integer[] ids);
}

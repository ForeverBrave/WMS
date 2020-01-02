package com.it.sys.mapper;

import com.it.sys.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Brave
 * @since 2020-01-02
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色ID删除sys_role_permission
     * @param id
     */
    void deleteRolePermissionByRid(Serializable id);

    /**
     * 根据角色ID删除sys_role_user
     * @param id
     */
    void deleteRoleUserByRid(Serializable id);
}

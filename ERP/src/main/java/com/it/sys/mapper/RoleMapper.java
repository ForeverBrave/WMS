package com.it.sys.mapper;

import com.it.sys.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

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

    /**
     * 根据角色ID查询当前角色拥有的所有的权限或菜单ID
     * @param roleId
     * @return
     */
    List<Integer> queryRolePermissionIdsByRid(Integer roleId);

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param pid
     */
    void ssaveRolePermission(@Param("rid") Integer rid, @Param("pid") Integer pid);

    /**
     * //根据用户id删除用户角色中间表的数据
     * @param id
     */
    void deleteRoleUserByUid(@Param("id") Serializable id);

    /**
     * 查询当前用户拥有的角色id集合
     * @param id
     * @return
     */
    List<Integer> queryUserRoleIdsByUid(Integer id);

    /**
     * 保存用户和角色之间的关系
     * @param uid
     * @param rid
     */
    void insertUserRole(@Param("uid") Integer uid,@Param("rid") Integer rid);
}

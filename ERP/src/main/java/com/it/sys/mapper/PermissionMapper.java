package com.it.sys.mapper;

import com.it.sys.domain.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Brave
 * @since 2019-12-23
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限菜单ID删除权限表各个角色关系表里的数据
     * @param id
     */
    void deleteRolePermissionByPid(@Param("id") Serializable id);
}

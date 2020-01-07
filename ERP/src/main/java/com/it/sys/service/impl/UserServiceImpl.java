package com.it.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.it.sys.domain.User;
import com.it.sys.mapper.RoleMapper;
import com.it.sys.mapper.UserMapper;
import com.it.sys.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean removeById(Serializable id) {

        //根据用户id删除用户角色中间表的数据
        this.roleMapper.deleteRoleUserByUid(id);
        //删除用户头像（如果是默认头像则不删除 ， 否则删除）
        return super.removeById(id);
    }

    /**
     * 保存用户和角色之间的关系
     * @param uid
     * @param ids
     */
    @Override
    public void saveUserRole(Integer uid, Integer[] ids) {
        //根据用户id删除sys_role_user里面的数据
        this.roleMapper.deleteRoleUserByUid(uid);
        if(null!=ids&&ids.length>0){
            for (Integer rid : ids) {
                this.roleMapper.insertUserRole(uid,rid);
            }
        }
    }
}

package com.it.bus.service.impl;

import com.it.bus.domain.Provider;
import com.it.bus.mapper.ProviderMapper;
import com.it.bus.service.ProviderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Brave
 * @since 2020-01-12
 */
@Service
@Transactional
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements ProviderService {

    @Override
    @CacheEvict(cacheNames = "provider",key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /*@Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }*/

    @Override
    @CachePut(cacheNames = "provider",key = "#result.id")
    public Provider saveProvider(Provider entity) {
        Provider provider = new Provider();
        super.save(entity);
        BeanUtils.copyProperties(entity,provider);
        return provider;
    }

    @Override
    @CachePut(cacheNames = "provider",key = "#entity.id")
    public Provider updateProviderById(Provider entity) {
        Provider provider = new Provider();
        super.updateById(entity);
        BeanUtils.copyProperties(entity,provider);
        return provider;
    }

    @Override
    @Cacheable(cacheNames = "provider",key = "#id")
    public Provider getById(Serializable id) {
        return super.getById(id);
    }
}

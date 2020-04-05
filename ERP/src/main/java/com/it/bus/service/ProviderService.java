package com.it.bus.service;

import com.it.bus.domain.Provider;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Brave
 * @since 2020-01-12
 */
public interface ProviderService extends IService<Provider> {

    Provider saveProvider(Provider entity);

    Provider updateProviderById(Provider entity);

}

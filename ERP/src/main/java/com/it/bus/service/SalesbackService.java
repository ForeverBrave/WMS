package com.it.bus.service;

import com.it.bus.domain.Salesback;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Brave
 * @since 2020-01-21
 */
public interface SalesbackService extends IService<Salesback> {

    void addSalesback(Integer id, Integer number, String remark);
}

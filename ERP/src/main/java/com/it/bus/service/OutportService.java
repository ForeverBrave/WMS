package com.it.bus.service;

import com.it.bus.domain.Outport;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Brave
 * @since 2020-01-16
 */
public interface OutportService extends IService<Outport> {

    /**
     * 退货
     * @param id
     * @param number
     * @param remark
     */
    void addOutport(Integer id, Integer number, String remark);
}

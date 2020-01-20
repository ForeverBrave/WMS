package com.it.bus.service.impl;

import com.it.bus.domain.Goods;
import com.it.bus.domain.Inport;
import com.it.bus.domain.Outport;
import com.it.bus.mapper.GoodsMapper;
import com.it.bus.mapper.InportMapper;
import com.it.bus.mapper.OutportMapper;
import com.it.bus.service.OutportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Brave
 * @since 2020-01-16
 */
@Service
@Transactional
public class OutportServiceImpl extends ServiceImpl<OutportMapper, Outport> implements OutportService {

    @Autowired
    private InportMapper inportMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 退货
     * @param id 进货单id
     * @param number 退货数量
     * @param remark 备注
     */
    @Override
    public void addOutport(Integer id, Integer number, String remark) {
        //1、根据进货id查询进货单信息
        Inport inport = this.inportMapper.selectById(id);
        //2、根据商品id查询商品信息
        Goods goods = this.goodsMapper.selectById(inport.getGoodsid());
        goods.setNumber(goods.getNumber()-number);
        this.goodsMapper.updateById(goods);
        //3、添加退货单信息
        Outport outport = new Outport();
        outport.setGoodsid(inport.getGoodsid());
        outport.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        outport.setOperateperson(user.getName());
        outport.setOutportprice(inport.getInportprice());
        outport.setOutputtime(new Date());
        outport.setPaytype(inport.getPaytype());
        outport.setProviderid(inport.getProviderid());
        outport.setRemark(remark);
        this.getBaseMapper().insert(outport);
    }
}

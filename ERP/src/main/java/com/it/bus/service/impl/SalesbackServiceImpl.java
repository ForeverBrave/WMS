package com.it.bus.service.impl;

import com.it.bus.domain.Goods;
import com.it.bus.domain.Sales;
import com.it.bus.domain.Salesback;
import com.it.bus.domain.Salesback;
import com.it.bus.mapper.GoodsMapper;
import com.it.bus.mapper.SalesMapper;
import com.it.bus.mapper.SalesbackMapper;
import com.it.bus.service.SalesbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Brave
 * @since 2020-01-21
 */
@Service
public class SalesbackServiceImpl extends ServiceImpl<SalesbackMapper, Salesback> implements SalesbackService {

    @Autowired
    private SalesMapper salesMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 退货
     * @param id 销售单id
     * @param number 退货数量
     * @param remark 备注
     */
    @Override
    public void addSalesback(Integer id, Integer number, String remark) {
        //1、根据销售id查询销售单信息
        Sales sales = this.salesMapper.selectById(id);
        sales.setNumber(sales.getNumber()-number);
        this.salesMapper.updateById(sales);
        //2、根据商品id查询商品信息
        Goods goods = this.goodsMapper.selectById(sales.getGoodsid());
        goods.setNumber(goods.getNumber()+number);
        this.goodsMapper.updateById(goods);
        //3、添加退货单信息
        Salesback salesback = new Salesback();
        salesback.setGoodsid(sales.getGoodsid());
        salesback.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        salesback.setOperateperson(user.getName());
        salesback.setSalebackprice(sales.getSaleprice());
        salesback.setSalesbacktime(new Date());
        salesback.setPaytype(sales.getPaytype());
        salesback.setCustomerid(sales.getCustomerid());
        salesback.setRemark(remark);
        this.getBaseMapper().insert(salesback);
    }

}

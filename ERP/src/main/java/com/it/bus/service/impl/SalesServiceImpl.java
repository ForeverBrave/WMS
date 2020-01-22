package com.it.bus.service.impl;

import com.it.bus.domain.Goods;
import com.it.bus.domain.Sales;
import com.it.bus.mapper.GoodsMapper;
import com.it.bus.mapper.SalesMapper;
import com.it.bus.service.SalesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.sys.common.ResultObj;
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
 * @since 2020-01-21
 */
@Service
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements SalesService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public boolean save(Sales entity) {
        //根据商品编号查询商品
        Goods goods=goodsMapper.selectById(entity.getGoodsid());
        //设置商品剩余数量 = 商品总数 - 销售数量
        if(goods.getNumber()>entity.getNumber()){
            goods.setNumber(goods.getNumber()-entity.getNumber());
        }else {
            return false;
        }
        goodsMapper.updateById(goods);
        //保存销售信息
        return super.save(entity);
    }

    @Override
    public boolean updateById(Sales entity) {
        //根据销售id查询销售
        Sales sales = this.baseMapper.selectById(entity.getId());
        //根据商品id查询商品信息
        Goods goods = this.goodsMapper.selectById(entity.getGoodsid());
        //修改库存算法  = 当前库存 + 销售单修改之前的数量 - 修改之后的数量
        goods.setNumber(goods.getNumber()+sales.getNumber()-entity.getNumber());
        this.goodsMapper.updateById(goods);
        //更新销售单
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        //根据销售id查询销售
        Sales sales = this.baseMapper.selectById(id);
        //根据商品id查询商品信息
        Goods goods = this.goodsMapper.selectById(sales.getGoodsid());
        //删除库存算法   当前库存 - 销售单的数量
        goods.setNumber(goods.getNumber()+sales.getNumber());
        this.goodsMapper.updateById(goods);
        return super.removeById(id);
    }

}

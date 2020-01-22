package com.it.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.bus.domain.Customer;
import com.it.bus.domain.Goods;
import com.it.bus.domain.Salesback;
import com.it.bus.domain.Provider;
import com.it.bus.service.CustomerService;
import com.it.bus.service.GoodsService;
import com.it.bus.service.SalesbackService;
import com.it.bus.service.ProviderService;
import com.it.bus.vo.SalesbackVo;
import com.it.sys.common.DataGridView;
import com.it.sys.common.ResultObj;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Brave
 * @since 2020-01-21
 */
@RestController
@RequestMapping("/salesback")
public class SalesbackController {

    @Autowired
    private SalesbackService salesbackService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllSalesback")
    public DataGridView loadAllSalesback(SalesbackVo salesbackVo){
        IPage<Salesback> salesbackPage = new Page<>(salesbackVo.getPage(), salesbackVo.getLimit());
        QueryWrapper<Salesback> wrapper = new QueryWrapper<>();
        wrapper.eq(salesbackVo.getCustomerid()!=null&&salesbackVo.getCustomerid()!=0,"providerid",salesbackVo.getCustomerid());
        wrapper.eq(salesbackVo.getGoodsid()!=null&&salesbackVo.getGoodsid()!=0,"goodsid",salesbackVo.getGoodsid());
        wrapper.ge(salesbackVo.getStartTime()!=null, "salesbacktime", salesbackVo.getStartTime());
        wrapper.le(salesbackVo.getEndTime()!=null, "salesbacktime", salesbackVo.getEndTime());
        wrapper.like(StringUtils.isNotBlank(salesbackVo.getOperateperson()),"operateperson",salesbackVo.getOperateperson());
        wrapper.like(StringUtils.isNotBlank(salesbackVo.getRemark()),"remark",salesbackVo.getRemark());
        wrapper.orderByDesc("salesbacktime");
        this.salesbackService.page(salesbackPage, wrapper);
        List<Salesback> records = salesbackPage.getRecords();
        for (Salesback salesback : records) {
            Customer customer = this.customerService.getById(salesback.getCustomerid());
            if(null!=customer) {
                salesback.setCustomername(customer.getCustomername());
            }
            Goods goods = this.goodsService.getById(salesback.getGoodsid());
            if(null!=goods) {
                salesback.setGoodsname(goods.getGoodsname());
                salesback.setSize(goods.getSize());
            }
        }
        return new DataGridView(salesbackPage.getTotal(), records);
    }

    /**
     * 添加退货信息
     * @param id 销售单id
     * @param number 退货数量
     * @param remark 备注
     * @return
     */
    @RequestMapping("addSalesback")
    public ResultObj addSalesback(Integer id, Integer number, String remark){
        try {
            this.salesbackService.addSalesback(id,number,remark);
            return ResultObj.OPERATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.OPERATE_ERROR;
        }
    }
    
}


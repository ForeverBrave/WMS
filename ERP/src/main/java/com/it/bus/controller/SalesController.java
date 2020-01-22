package com.it.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.bus.domain.Goods;
import com.it.bus.domain.Sales;
import com.it.bus.domain.Customer;
import com.it.bus.service.GoodsService;
import com.it.bus.service.SalesService;
import com.it.bus.service.CustomerService;
import com.it.bus.vo.InportVo;
import com.it.bus.vo.SalesVo;
import com.it.sys.common.DataGridView;
import com.it.sys.common.ResultObj;
import com.it.sys.common.WebUtils;
import com.it.sys.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllSales")
    public DataGridView loadAllSales(SalesVo salesVo){
        IPage<Sales> salesPage = new Page<>(salesVo.getPage(), salesVo.getLimit());
        QueryWrapper<Sales> wrapper = new QueryWrapper<>();
        wrapper.eq(salesVo.getCustomerid()!=null&&salesVo.getCustomerid()!=0,"customerid",salesVo.getCustomerid());
        wrapper.eq(salesVo.getGoodsid()!=null&&salesVo.getGoodsid()!=0,"goodsid",salesVo.getGoodsid());
        wrapper.ge(salesVo.getStartTime()!=null, "salestime", salesVo.getStartTime());
        wrapper.le(salesVo.getEndTime()!=null, "salestime", salesVo.getEndTime());
        wrapper.like(StringUtils.isNotBlank(salesVo.getOperateperson()),"operateperson",salesVo.getOperateperson());
        wrapper.like(StringUtils.isNotBlank(salesVo.getRemark()),"remark",salesVo.getRemark());
        wrapper.orderByDesc("salestime");
        this.salesService.page(salesPage, wrapper);
        List<Sales> records = salesPage.getRecords();
        for (Sales sales : records) {
            Customer customer = this.customerService.getById(sales.getCustomerid());
            if(null!=customer) {
                sales.setCustomername(customer.getCustomername());
            }
            Goods goods = this.goodsService.getById(sales.getGoodsid());
            if(null!=goods) {
                sales.setGoodsname(goods.getGoodsname());
                sales.setSize(goods.getSize());
            }
        }
        return new DataGridView(salesPage.getTotal(), records);
    }

    /**
     * 添加
     * @param salesVo
     * @return
     */
    @RequestMapping("addSales")
    public ResultObj addSales(SalesVo salesVo){
        try {
            salesVo.setSalestime(new Date());
            User user = (User) WebUtils.getSession().getAttribute("user");
            salesVo.setOperateperson(user.getName());
            this.salesService.save(salesVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 更新
     * @param salesVo
     * @return
     */
    @RequestMapping("updateSales")
    public ResultObj updateSales(SalesVo salesVo){
        try {
            this.salesService.updateById(salesVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("deleteSales")
    public ResultObj deleteSales(Integer id){
        try {
            this.salesService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}


package com.it.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.bus.domain.Goods;
import com.it.bus.domain.Outport;
import com.it.bus.domain.Provider;
import com.it.bus.service.GoodsService;
import com.it.bus.service.OutportService;
import com.it.bus.service.ProviderService;
import com.it.bus.vo.OutportVo;
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
 * @since 2020-01-16
 */
@RestController
@RequestMapping("outport")
public class OutportController {

    @Autowired
    private OutportService outportService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllOutport")
    public DataGridView loadAllOutport(OutportVo outportVo){
        IPage<Outport> outportPage = new Page<>(outportVo.getPage(), outportVo.getLimit());
        QueryWrapper<Outport> wrapper = new QueryWrapper<>();
        wrapper.eq(outportVo.getProviderid()!=null&&outportVo.getProviderid()!=0,"providerid",outportVo.getProviderid());
        wrapper.eq(outportVo.getGoodsid()!=null&&outportVo.getGoodsid()!=0,"goodsid",outportVo.getGoodsid());
        wrapper.ge(outportVo.getStartTime()!=null, "outputtime", outportVo.getStartTime());
        wrapper.le(outportVo.getEndTime()!=null, "outputtime", outportVo.getEndTime());
        wrapper.like(StringUtils.isNotBlank(outportVo.getOperateperson()),"operateperson",outportVo.getOperateperson());
        wrapper.like(StringUtils.isNotBlank(outportVo.getRemark()),"remark",outportVo.getRemark());
        wrapper.orderByDesc("outputtime");
        this.outportService.page(outportPage, wrapper);
        List<Outport> records = outportPage.getRecords();
        for (Outport outport : records) {
            Provider provider = this.providerService.getById(outport.getProviderid());
            if(null!=provider) {
                outport.setProvidername(provider.getProvidername());
            }
            Goods goods = this.goodsService.getById(outport.getGoodsid());
            if(null!=goods) {
                outport.setGoodsname(goods.getGoodsname());
                outport.setSize(goods.getSize());
            }
        }
        return new DataGridView(outportPage.getTotal(), records);
    }

    /**
     * 添加退货信息
     * @param id 进货单id
     * @param number 退货数量
     * @param remark 备注
     * @return
     */
    @RequestMapping("addOutport")
    public ResultObj addOutport(Integer id,Integer number,String remark){
        try {
            this.outportService.addOutport(id,number,remark);
            return ResultObj.OPERATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.OPERATE_ERROR;
        }
    }
}


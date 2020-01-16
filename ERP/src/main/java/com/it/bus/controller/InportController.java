package com.it.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.bus.domain.Goods;
import com.it.bus.domain.Inport;
import com.it.bus.domain.Provider;
import com.it.bus.service.GoodsService;
import com.it.bus.service.InportService;
import com.it.bus.service.ProviderService;
import com.it.bus.vo.GoodsVo;
import com.it.bus.vo.InportVo;
import com.it.sys.common.*;
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
 * @since 2020-01-16
 */
@RestController
@RequestMapping("inport")
public class InportController {

    @Autowired
    private InportService inportService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllInport")
    public DataGridView loadAllInport(InportVo inportVo){
        IPage<Inport> inportPage = new Page<>(inportVo.getPage(), inportVo.getLimit());
        QueryWrapper<Inport> wrapper = new QueryWrapper<>();
        wrapper.eq(inportVo.getProviderid()!=null&&inportVo.getProviderid()!=0,"providerid",inportVo.getProviderid());
        wrapper.eq(inportVo.getGoodsid()!=null&&inportVo.getGoodsid()!=0,"goodsid",inportVo.getGoodsid());
        wrapper.ge(inportVo.getStartTime()!=null, "inporttime", inportVo.getStartTime());
        wrapper.le(inportVo.getEndTime()!=null, "inporttime", inportVo.getEndTime());
        wrapper.like(StringUtils.isNotBlank(inportVo.getOperateperson()),"operateperson",inportVo.getOperateperson());
        wrapper.like(StringUtils.isNotBlank(inportVo.getRemark()),"remark",inportVo.getRemark());
        wrapper.orderByDesc("inporttime");
        this.inportService.page(inportPage, wrapper);
        List<Inport> records = inportPage.getRecords();
        for (Inport inport : records) {
            Provider provider = this.providerService.getById(inport.getProviderid());
            if(null!=provider) {
                inport.setProvidername(provider.getProvidername());
            }
            Goods goods = this.goodsService.getById(inport.getGoodsid());
            if(null!=goods) {
                inport.setGoodsname(goods.getGoodsname());
                inport.setSize(goods.getSize());
            }
        }
        return new DataGridView(inportPage.getTotal(), records);
    }

    /**
     * 添加
     * @param inportVo
     * @return
     */
    @RequestMapping("addInport")
    public ResultObj addInport(InportVo inportVo){
        try {
            inportVo.setInporttime(new Date());
            User user = (User) WebUtils.getSession().getAttribute("user");
            inportVo.setOperateperson(user.getName());
            this.inportService.save(inportVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 更新
     * @param inportVo
     * @return
     */
    @RequestMapping("updateInport")
    public ResultObj updateInport(InportVo inportVo){
        try {
            this.inportService.updateById(inportVo);
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
    @RequestMapping("deleteInport")
    public ResultObj deleteInport(Integer id){
        try {
            this.inportService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}


package com.it.bus.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.bus.domain.Goods;
import com.it.bus.domain.Provider;
import com.it.bus.service.GoodsService;
import com.it.bus.service.ProviderService;
import com.it.bus.vo.GoodsVo;
import com.it.sys.common.AppFileUtils;
import com.it.sys.common.Constast;
import com.it.sys.common.DataGridView;
import com.it.sys.common.ResultObj;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Brave
 * @since 2020-01-13
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ProviderService providerService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllGoods")
    public DataGridView loadAllGoods(GoodsVo goodsVo){
        IPage<Goods> goodsPage = new Page<>(goodsVo.getPage(),goodsVo.getLimit());
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq(goodsVo.getProviderid()!=null&&goodsVo.getProviderid()!=0,"providerid",goodsVo.getProviderid());
        wrapper.like(StringUtils.isNotBlank(goodsVo.getGoodsname()),"goodsname",goodsVo.getGoodsname());
        wrapper.like(StringUtils.isNotBlank(goodsVo.getProductcode()),"productcode",goodsVo.getProductcode());
        wrapper.like(StringUtils.isNotBlank(goodsVo.getPromitcode()),"promitcode",goodsVo.getPromitcode());
        wrapper.like(StringUtils.isNotBlank(goodsVo.getDescription()),"description",goodsVo.getDescription());
        wrapper.like(StringUtils.isNotBlank(goodsVo.getSize()),"size",goodsVo.getSize());
        this.goodsService.page(goodsPage,wrapper);
        List<Goods> records = goodsPage.getRecords();
        for (Goods goods : records) {
            Provider provider = this.providerService.getById(goods.getProviderid());
            if(null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView(goodsPage.getTotal(),records);
    }

    /**
     * 添加
     * @param goodsVo
     * @return
     */
    @RequestMapping("addGoods")
    public ResultObj addGoods(GoodsVo goodsVo){
        try {
            if (goodsVo.getGoodsimg()!=null&&goodsVo.getGoodsimg().endsWith("_temp")){
                String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                goodsVo.setGoodsimg(newName);
            }
            this.goodsService.save(goodsVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 更新
     * @param goodsVo
     * @return
     */
    @RequestMapping("updateGoods")
    public ResultObj updateGoods(GoodsVo goodsVo){
        try {
            //说明不是默认图片
            if(!(goodsVo.getGoodsimg()!=null&& Constast.IMAGES_DEFAULTHOODSIMG_PNG.equals(goodsVo.getGoodsimg()))){
                if (goodsVo.getGoodsimg().endsWith("_temp")){
                    String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                    goodsVo.setGoodsimg(newName);
                    //删除原先图片
                    String oldPath = this.goodsService.getById(goodsVo.getId()).getGoodsimg();
                    AppFileUtils.removeFileByPath(oldPath);
                }
            }
            this.goodsService.updateById(goodsVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     * @return
     */
    @RequestMapping("deleteGoods")
    public ResultObj deleteGoods(Integer id,String goodsimg){
        try {
            //删除源文件
            AppFileUtils.removeFileByPath(goodsimg);
            this.goodsService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 加载所有可用的商品
     * @return
     */
    @RequestMapping("loadAllGoodsForSelect")
    public DataGridView loadAllGoodsForSelect(){
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Goods> list = this.goodsService.list(wrapper);
        for (Goods goods : list) {
            Provider provider = this.providerService.getById(goods.getProviderid());
            if(null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView(list);
    }

    /**
     * 根据供应商id查询商品信息
     * @return
     */
    @RequestMapping("loadGoodsByProviderId")
    public DataGridView loadGoodsByProviderId(Integer providerid){
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("available", Constast.AVAILABLE_TRUE);
        wrapper.eq(providerid!=null,"providerid",providerid);
        List<Goods> list = this.goodsService.list(wrapper);
        for (Goods goods : list) {
            Provider provider = this.providerService.getById(goods.getProviderid());
            if(null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView(list);
    }


}


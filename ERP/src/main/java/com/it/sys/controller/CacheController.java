package com.it.sys.controller;

import com.it.sys.cache.CachePool;
import com.it.sys.common.CacheBean;
import com.it.sys.common.DataGridView;
import com.it.sys.common.ResultObj;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 缓存管理控制器
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/20 19:59
 */
@RestController
@RequestMapping("cache")
public class CacheController {

    public static volatile Map<String, Object> CACHE_CONTAINER = CachePool.CACHE_CONTAINER;

    /**
     * 查询所有缓存
     */
    @RequestMapping("loadAllCache")
    public DataGridView loadAllCache() {
        List<CacheBean> list=new ArrayList<>();

        Set<Map.Entry<String, Object>> entrySet = CACHE_CONTAINER.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            list.add(new CacheBean(entry.getKey(), entry.getValue()));
        }
        return new DataGridView(list);
    }

    /**
     * 删除缓存
     */
    @RequestMapping("deleteCache")
    public ResultObj deleteCache(String key){
        CachePool.removeCacheByKey(key);
        return ResultObj.DELETE_SUCCESS;
    }

    /**
     * 清空缓存
     */
    @RequestMapping("removeAllCache")
    public ResultObj removeAllCache(){
        CachePool.removeAll();
        return ResultObj.DELETE_SUCCESS;
    }

    /**
     * 同步缓存
     */
    @RequestMapping("syncCache")
    public ResultObj syncCache() {
        CachePool.syncData();
        return ResultObj.OPERATE_SUCCESS;
    }

}

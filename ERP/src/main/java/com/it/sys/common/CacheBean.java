package com.it.sys.common;

import com.alibaba.fastjson.JSON;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/20 20:04
 */

public class CacheBean {

    private String key;

    private Object value;

    public CacheBean() {
        // TODO Auto-generated constructor stub
    }

    public CacheBean(String key, Object value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return JSON.toJSON(value).toString();
    }

    public void setValue(Object value) {
        this.value = value;
    }

}

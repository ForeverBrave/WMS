package com.it.bus.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Brave
 * @since 2020-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_sales")
public class Sales implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer customerid;

    private String paytype;

    private Date salestime;

    private String operateperson;

    private Integer number;

    private String remark;

    private Double saleprice;

    //private BigDecimal saleprice;

    private Integer goodsid;

    @TableField(exist = false)
    private String customername;

    @TableField(exist = false)
    private String goodsname;

    @TableField(exist = false)
    private String size;


}

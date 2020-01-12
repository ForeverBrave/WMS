package com.it.bus.vo;

import com.it.bus.domain.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/12 15:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerVo extends Customer {

    private static final long serialVersionUID = 1L;

    private Integer page = 1;
    private Integer limit = 10;

    private Integer [] ids;

}

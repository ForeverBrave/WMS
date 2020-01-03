package com.it.sys.vo;

import com.it.sys.domain.Dept;
import com.it.sys.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 15:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo extends User{

    private static final long serialVersionUID = 1L;

    private Integer page = 1;
    private Integer limit = 10;

}

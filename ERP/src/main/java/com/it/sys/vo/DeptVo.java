package com.it.sys.vo;

import com.it.sys.domain.Dept;
import com.it.sys.domain.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 15:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeptVo extends Dept {

    private static final long serialVersionUID = 1L;

    private Integer page = 1;
    private Integer limit = 10;

}

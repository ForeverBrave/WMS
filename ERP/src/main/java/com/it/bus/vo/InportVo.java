package com.it.bus.vo;

import com.it.bus.domain.Inport;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/12 15:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InportVo extends Inport {

    private static final long serialVersionUID = 1L;

    private Integer page = 1;
    private Integer limit = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}

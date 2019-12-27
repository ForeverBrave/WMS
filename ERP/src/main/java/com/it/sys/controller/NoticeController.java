package com.it.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.sys.common.DataGridView;
import com.it.sys.domain.Notice;
import com.it.sys.service.NoticeService;
import com.it.sys.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Brave
 * @since 2019-12-27
 */
@RestController
@RequestMapping("/sys/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("loadAllNotice")
    public DataGridView loadAllNotice(NoticeVo noticeVo){
        IPage<Notice> noticePage = new Page<>(noticeVo.getPage(),noticeVo.getLimit());
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(noticeVo.getTitle()),"title",noticeVo.getTitle());
        wrapper.like(StringUtils.isNotBlank(noticeVo.getOpername()),"opername",noticeVo.getOpername());
        //大于开始时间
        wrapper.ge(noticeVo.getStartTime()!=null,"createtime",noticeVo.getStartTime());
        //小于最后时间
        wrapper.le(noticeVo.getEndTime()!=null,"createtime",noticeVo.getEndTime());
        wrapper.orderByDesc("createtime");
        this.noticeService.page(noticePage,wrapper);
        return new DataGridView(noticePage.getTotal(),noticePage.getRecords());
    }

}


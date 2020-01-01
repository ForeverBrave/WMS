package com.it.sys.service.impl;

import com.it.sys.domain.Loginfo;
import com.it.sys.mapper.LoginfoMapper;
import com.it.sys.service.LoginfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Brave
 * @since 2019-12-24
 */
@Service
@Transactional
public class LoginfoServiceImpl extends ServiceImpl<LoginfoMapper, Loginfo> implements LoginfoService {

}

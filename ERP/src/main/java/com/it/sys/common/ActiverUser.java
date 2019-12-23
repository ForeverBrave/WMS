package com.it.sys.common;

import com.it.sys.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2019/12/23 12:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiverUser {

    private User user;
    private List<String> roles;
    private List<String> permissions;

}

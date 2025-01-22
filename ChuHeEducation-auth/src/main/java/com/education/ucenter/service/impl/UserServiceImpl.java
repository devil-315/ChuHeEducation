package com.education.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.ucenter.mapper.XcUserMapper;
import com.education.ucenter.model.dto.AuthParamsDto;
import com.education.ucenter.model.dto.XcUserExt;
import com.education.ucenter.model.po.XcUser;
import com.education.ucenter.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ClassName：UserServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/21
 * @Description:
 * @version: 1.0
 */
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * @description 根据账号查询用户信息
     * @param s  账号
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json转为AuthParamsDto对象
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
        }catch (Exception e){
            throw new RuntimeException("请求参数不符合要求");
        }

        //认证类型
        String authType = authParamsDto.getAuthType();
        //根据认证类型从spring容器取出指定的bean
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用统一的execute方法完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装XcUserExt为UserDetails
        UserDetails userDetails = getUserPrincipal(xcUserExt);

        return userDetails;
    }

    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     * @return XcUser 用户信息
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities= {"test"};
        //密码
        String password = xcUser.getPassword();
        xcUser.setPassword(null);
        //将用户信息转json
        String userJSON = JSON.toJSONString(xcUser);
        //创建UserDetails对象,权限信息待实现授权功能时再向UserDetail中加入
        UserDetails userDetails = User.withUsername(userJSON).password(password).authorities(authorities).build();
        return userDetails;
    }
}

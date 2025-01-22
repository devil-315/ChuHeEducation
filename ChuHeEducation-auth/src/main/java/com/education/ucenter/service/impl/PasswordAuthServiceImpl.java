package com.education.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.ucenter.feignclient.CheckCodeClient;
import com.education.ucenter.mapper.XcUserMapper;
import com.education.ucenter.model.dto.AuthParamsDto;
import com.education.ucenter.model.dto.XcUserExt;
import com.education.ucenter.model.po.XcUser;
import com.education.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ClassName：PasswordAuthServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/22
 * @Description:
 * @version: 1.0
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //账号
        String userName = authParamsDto.getUsername();

        //前端输入的验证码
        String checkcode = authParamsDto.getCheckcode();
        //验证码对应的key
        String checkcodekey = authParamsDto.getCheckcodekey();

        if(StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");

        }

        //远程调用验证码服务接口校验验证码
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null || !verify){
            throw new RuntimeException("验证码输入错误");
        }

        //账号是否存在
        //根据username账号查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, userName));
        //查询到用户不存在，返回null即可， spring security框架抛出异常用户不存在
        if(xcUser==null){
            throw new RuntimeException("账号不存在");
        }
        //验证密码是否正确
        String passwordDb  =xcUser.getPassword();
        //用户输入的密码
        String passwordFrom = authParamsDto.getPassword();
        //校验密码
        boolean matches = passwordEncoder.matches(passwordFrom, passwordDb);
        if(!matches){
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;

    }
}

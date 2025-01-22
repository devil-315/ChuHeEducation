package com.education.ucenter.service;

import com.education.ucenter.model.dto.AuthParamsDto;
import com.education.ucenter.model.dto.XcUserExt;

/**
 * @description 认证service
 */
public interface AuthService {

    /**
    * @description 认证方法
    * @param authParamsDto 认证参数
   */
    XcUserExt execute(AuthParamsDto authParamsDto);

}
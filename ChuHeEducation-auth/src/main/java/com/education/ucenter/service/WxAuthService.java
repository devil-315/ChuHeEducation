package com.education.ucenter.service;

import com.education.ucenter.model.po.XcUser;

/**
 * @description 微信认证接口
 */
public interface WxAuthService {

    public XcUser wxAuth(String code);

}
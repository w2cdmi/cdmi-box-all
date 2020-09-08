package com.huawei.sharedrive.cloudapp.cmb.user.service;

import com.huawei.sharedrive.cloudapp.exception.InternalServerErrorException;
import com.huawei.sharedrive.cloudapp.exception.LoginAuthFailedException;
import com.huawei.sharedrive.cloudapp.httpclient.rest.request.RestLoginResponse;
import com.huawei.sharedrive.cloudapp.oauth2.domain.UserToken;

public interface UserCMBLoginService
{
    RestLoginResponse authCmbUser(String data, String token, String appId, UserToken userToken)
        throws LoginAuthFailedException, InternalServerErrorException;
}

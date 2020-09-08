package com.huawei.sharedrive.uam.openapi.manager;

import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.RequestSearchUser;

public interface TokenMeApiCheckManager
{
    void checkSearchUserParameter(RequestSearchUser searchRequest);
    
    void checkLdapUserUpdateReq(BasicUserUpdateRequest ruser);
}

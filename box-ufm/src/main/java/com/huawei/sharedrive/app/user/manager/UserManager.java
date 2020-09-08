package com.huawei.sharedrive.app.user.manager;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.user.domain.User;

public interface UserManager
{
    User deleteUserById(long userId, String[] akArr);
    
    RestUserCreateRequest addUser (RestUserCreateRequest requestUser ,String[] akArr , Account account);

	RestUserCreateRequest addWxUser(RestUserCreateRequest ruser,String appId, String[] akArr);
}

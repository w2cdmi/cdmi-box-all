package com.huawei.sharedrive.uam.accountuser.manager;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;

public interface UserAccountCheckManager
{
    void checkUpdateUserListPara(UpdateUserAccountList updateUserList, String appId);
    
    void checkUserAccountStatus(int status);
    
    boolean isUpdateAndSetUserAccount(UserAccount selUser, RestUserUpdateRequest rUser);
}

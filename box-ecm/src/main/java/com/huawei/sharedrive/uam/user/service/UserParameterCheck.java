package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import com.huawei.sharedrive.uam.user.domain.User;

public interface UserParameterCheck
{
    void checkUpdateUserListParament(UpdateUserAccountList updateUserList, String appId);
    
    void preCheckImportUserParameter(User user);
    
    void checkUserParameter(User user);
    
    void checkCreateUserParament(RestUserUpdateRequest user, String appId, boolean isRest);
    
    void checkUpdateUserParament(BasicUserUpdateRequest user, String appId);
    
    User fillCreateUser(RestUserUpdateRequest ruser, String appId);
    
    void checkADUserParament(RestUserUpdateRequest user, String appId);
    
    void checkBandWidthParament(Long uploadBandWidth, Long downloadBandWidth);
}

package com.huawei.sharedrive.uam.enterpriseuser.manager;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

public interface EnterpriseUserCheckManager
{
    boolean isUpdateByLdap(EnterpriseUser user, EnterpriseUser ldapUser);
    
    boolean isLoginNameChanged(String name, String ldapName);
    
    void checkAndSetPassword(EnterpriseUser enterpriseUser, String newPassword, String oldPassword);
    
    boolean isUpdateAndSetEnterpriseUser(EnterpriseUser selUser, EnterpriseUser updateUser);
}

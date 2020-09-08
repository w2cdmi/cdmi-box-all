package com.huawei.sharedrive.uam.oauth2.manager;

import com.huawei.sharedrive.uam.exception.AuthFailedException;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface AuthManager
{
    EnterpriseAccount checkAppToken(String authorization, String date) throws AuthFailedException;
}

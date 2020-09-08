package com.huawei.sharedrive.uam.enterpriseuseraccount.service;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;

import pw.cdmi.box.domain.Limit;

public interface EnterpriseUserAccountService
{
    List<ResponseUser> listUser(String appId, long accountid, long enterpriseId, String filter, Limit limit);
    
    int listUserCount(long accountid, long enterpriseId, String filter, Limit limit);
}

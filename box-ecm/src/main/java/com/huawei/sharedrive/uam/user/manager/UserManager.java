package com.huawei.sharedrive.uam.user.manager;

import java.util.List;

import pw.cdmi.box.http.request.RestRegionInfo;

public interface UserManager
{
    
    List<RestRegionInfo> getRegionInfo(String appId);
}

package com.huawei.sharedrive.uam.statistics.manager;

import java.util.List;

import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.user.domain.User;

import pw.cdmi.box.domain.Pager;
import pw.cdmi.box.http.request.RestRegionInfo;

public interface AppStatisticsManager
{
    
    List<User> listStatisticsByAppId(String appId);
    
    Pager<User> getRankedUser(Pager<User> userPage, User user, RankRequest request);
    
    List<RestRegionInfo> getRegionInfo(String appId);
    
}

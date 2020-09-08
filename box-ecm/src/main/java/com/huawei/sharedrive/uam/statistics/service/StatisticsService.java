package com.huawei.sharedrive.uam.statistics.service;

import java.util.List;

import pw.cdmi.box.http.request.RestRegionInfo;

public interface StatisticsService
{
    List<RestRegionInfo> getRegionInfo();
    
}

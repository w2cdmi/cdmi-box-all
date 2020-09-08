package com.huawei.sharedrive.app.statistics.service;

import com.huawei.sharedrive.app.openapi.domain.statistics.RestStatisticsRequest;
import com.huawei.sharedrive.app.statistics.domain.AppStatisticsInfo;
import com.huawei.sharedrive.app.user.domain.User;

public interface StatisticsService
{
    AppStatisticsInfo getStatisticsInfo(RestStatisticsRequest restStatistiscRequest, User user);
}

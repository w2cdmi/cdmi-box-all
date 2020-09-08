package com.huawei.sharedrive.app.statistics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.openapi.domain.statistics.RestStatisticsRequest;
import com.huawei.sharedrive.app.statistics.domain.AppStatisticsInfo;
import com.huawei.sharedrive.app.statistics.service.StatisticsService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;

@Component
@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService
{
    /**
     * 已开户开户用户状态
     */
    private static final byte STATUS_LOGINED_USER = 0;
    
    /**
     * 未开户用户状态
     */
    private static final byte STATUS_NOT_LOGINED_USER = 1;
    
    /**
     * 团队空间
     */
    private static final byte STATUS_TEAMSPACE = 2;
    
    @Autowired
    private UserService userService;
    
    @Override
    public AppStatisticsInfo getStatisticsInfo(RestStatisticsRequest restStatisticsRequest, User user)
    {
        AppStatisticsInfo statistics = new AppStatisticsInfo();
        String type = restStatisticsRequest.getType();
        
        // 根据请求参数type的值组装参数map
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", user);
        if ("user".equals(type))
        {
            map.put("numLimit1", STATUS_LOGINED_USER);
            map.put("numLimit2", STATUS_NOT_LOGINED_USER);
        }
        else if ("teamspace".equals(type))
        {
            map.put("numLimit1", STATUS_TEAMSPACE);
        }
        // 获取空间总数
        long spaceCount = userService.countSpaceTotal(map);
        statistics.setSpaceCount(spaceCount);
        
        List<User> users = userService.getUsedCapacity(map);
        long totalFileCount = 0;
        long totalSpaceUsed = 0;
        for (User u : users)
        {
            totalFileCount += u.getFileCount();
            totalSpaceUsed += u.getSpaceUsed();
        }
        statistics.setFileCount(totalFileCount);
        statistics.setSpaceUsed(totalSpaceUsed);
        return statistics;
    }
    
}

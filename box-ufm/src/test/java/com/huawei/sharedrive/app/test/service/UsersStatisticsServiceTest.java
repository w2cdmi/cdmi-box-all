package com.huawei.sharedrive.app.test.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.openapi.domain.statistics.MilestioneInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsInfo;
import com.huawei.sharedrive.app.statistics.service.UsersStatisticsService;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class UsersStatisticsServiceTest extends AbstractSpringTest
{
    @Autowired
    private UsersStatisticsService usersStatisticsService;
    
    @Test
    public void getUserCurrentStatistics()
    {
        RestUserCurrentStatisticsRequest request = new RestUserCurrentStatisticsRequest();
        request.setGroupBy("all");
        List<UserCurrentStatisticsInfo> list = usersStatisticsService.getUserCurrentStatistics(request);
        System.out.println(list.size());
    }
    
    @Test
    public void getUserClusterStatistics()
    {
        ArrayList<MilestioneInfo> lst = new ArrayList<MilestioneInfo>();
        MilestioneInfo info = new MilestioneInfo();
        info.setMilestone(3L);
        lst.add(info);
        info = new MilestioneInfo();
        info.setMilestone(2L);
        lst.add(info);
        List<UserClusterStatisticsInfo> list = usersStatisticsService.getUserClusterStatistics(lst);
        System.out.println(list.size());
    }
    
    
    @Test
    public void getUserHistoryStatistics()
    {
        RestUserHistoryStatisticsRequest request = new RestUserHistoryStatisticsRequest();
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -100);
        long beginTime = ca.getTimeInMillis();
        ca.add(Calendar.DAY_OF_MONTH, 400);
        long endTime = ca.getTimeInMillis();
        request.setBeginTime(beginTime);
        request.setEndTime(endTime);
        request.setInterval("season");
        List<UserHistoryStatisticsInfo> list = usersStatisticsService.getUserHistoryStatistics(request);
        System.out.println(list.size());
        
        
    }
}

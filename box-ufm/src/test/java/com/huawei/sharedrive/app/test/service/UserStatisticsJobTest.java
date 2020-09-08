package com.huawei.sharedrive.app.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.statistics.job.UserStatisticsJob;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class UserStatisticsJobTest extends AbstractSpringTest
{
    @Autowired
    private UserStatisticsJob job;
    
    @Test
    public void testAddHistioryData()
    {
        try
        {
            job.doTask(null, null);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

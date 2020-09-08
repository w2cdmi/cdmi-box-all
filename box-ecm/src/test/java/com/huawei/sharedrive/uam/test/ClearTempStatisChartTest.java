package com.huawei.sharedrive.uam.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.uam.statistics.job.ClearTempStatisChartJob;

public class ClearTempStatisChartTest extends AbstractSpringTest {
	
	@Autowired
    private ClearTempStatisChartJob job;
    
    @Test
    public void testClearTempStatisChart()
    {
        try
        {
            job.doTask(null, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

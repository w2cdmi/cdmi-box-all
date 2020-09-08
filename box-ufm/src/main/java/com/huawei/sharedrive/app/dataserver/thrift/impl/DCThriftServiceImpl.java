/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.dataserver.thrift.impl;

import java.util.Calendar;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.statistics.dao.TempConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.TempConcStatistics;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;
import com.huawei.sharedrive.thrift.app2dc.DCThriftService;

/**
 * 提供给DC访问的Thrift接口
 * 
 * @author s90006125
 * 
 */
public class DCThriftServiceImpl implements DCThriftService.Iface
{
    
    @Autowired
    private TempConcStatisticsDAO tempConcStatisticsDAO;
    
    @Override
    public void reportStatistics(String host, int maxUpload, int maxDownload) throws TException
    {
        Calendar calendar = Calendar.getInstance();
        TempConcStatistics tempConcStatistics = new TempConcStatistics();
        int day = StatisticsDateUtils.getDay(calendar);
        tempConcStatistics.setDay(day);
        int timeUnit = StatisticsDateUtils.getTimeUnitBy5Min(calendar);
        tempConcStatistics.setTimeUnit(timeUnit);
        tempConcStatistics.setHost(host);
        tempConcStatistics.setMaxUpload(maxUpload);
        tempConcStatistics.setMaxDownload(maxDownload);
        TempConcStatistics dbStatistics = this.tempConcStatisticsDAO.get(day, host, timeUnit);
        if (null == dbStatistics)
        {
            this.tempConcStatisticsDAO.insert(tempConcStatistics);
        }
        else
        {
            updateTempConcData(tempConcStatistics, dbStatistics);
        }
    }
    
    private void updateTempConcData(TempConcStatistics tempConcStatistics, TempConcStatistics dbStatistics)
    {
        boolean changed = false;
        if (tempConcStatistics.getMaxDownload() > dbStatistics.getMaxDownload())
        {
            dbStatistics.setMaxDownload(tempConcStatistics.getMaxDownload());
            changed = true;
        }
        if (tempConcStatistics.getMaxUpload() > dbStatistics.getMaxUpload())
        {
            dbStatistics.setMaxUpload(tempConcStatistics.getMaxUpload());
            changed = true;
        }
        if (changed)
        {
            this.tempConcStatisticsDAO.update(dbStatistics);
        }
    }
    
}

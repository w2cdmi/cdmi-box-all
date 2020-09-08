package com.huawei.sharedrive.uam.statistics.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.httpclient.rest.StatisticsHttpClient;
import com.huawei.sharedrive.uam.statistics.service.StatisticsAccesskeyService;
import com.huawei.sharedrive.uam.statistics.service.StatisticsService;

import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.core.restrpc.RestClient;

@Service
public class StatisticsServiceImpl implements StatisticsService
{
    
    private StatisticsHttpClient statisticsHttpClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @PostConstruct
    public void init()
    {
        this.statisticsHttpClient = new StatisticsHttpClient(ufmClientService, statisticsAccesskeyService);
    }
    
    @Override
    public List<RestRegionInfo> getRegionInfo()
    {
        return statisticsHttpClient.getRegionInfo();
    }
    
}

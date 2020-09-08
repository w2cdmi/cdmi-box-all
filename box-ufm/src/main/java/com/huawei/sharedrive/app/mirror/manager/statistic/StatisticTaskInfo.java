package com.huawei.sharedrive.app.mirror.manager.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.utils.JsonUtils;


/**
 * 统计任务信息
 * 
 * @author c00287749
 * 
 */
public class StatisticTaskInfo
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticTaskInfo.class);
    
    public static final long INIT_DEFAULT = -1L;
    
    private int policyId = -1;
    
    private long accountId =INIT_DEFAULT;
    
    private String appId;
    
    private long userId = INIT_DEFAULT;
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public static String toJsonStr(StatisticTaskInfo info)
    {
        try
        {
            return JsonUtils.toJson(info);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
    
    public static StatisticTaskInfo toObject(String jsonStr)
    {
        try
        {
            return JsonUtils.stringToObject(jsonStr, StatisticTaskInfo.class);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
    
}

package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

import com.huawei.sharedrive.uam.util.PropertiesUtils;

public class NetWorkRegionIpExcelImport implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final int IMPORT_SUCCESS = 1;
    
    public static final int IMPORT_FAILED = 2;
    
    public static final int IMPORTING = 3;
    
    public static final int MAX_IMPORT = Integer.parseInt(PropertiesUtils.getProperty("max.import.count", "5"));
    
    private long id;
    
    private long accountId;
    
    private byte[] resultData;
    
    private Date runtime;
    
    private Date completeTime;
    
    private int status;
    
    private String appId;
    
    private long succeededCount;
    
    private long failedCount;
    
    private long totalCount;
    
    private String resultStr;
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setRuntime(Date runtime)
    {
        if (null != runtime)
        {
            this.runtime = (Date) runtime.clone();
        }
        else
        {
            this.runtime = null;
        }
    }
    
    public Date getRuntime()
    {
        if (null != runtime)
        {
            return (Date) runtime.clone();
        }
        return null;
    }
    
    public void setCompleteTime(Date completeTime)
    {
        if (null != completeTime)
        {
            this.completeTime = (Date) completeTime.clone();
        }
        else
        {
            this.completeTime = null;
        }
    }
    
    public Date getCompleteTime()
    {
        if (null != completeTime)
        {
            return (Date) completeTime.clone();
        }
        return null;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public long getSucceededCount()
    {
        return succeededCount;
    }
    
    public void setSucceededCount(long succeededCount)
    {
        this.succeededCount = succeededCount;
    }
    
    public long getFailedCount()
    {
        return failedCount;
    }
    
    public void setFailedCount(long failedCount)
    {
        this.failedCount = failedCount;
    }
    
    public byte[] getResultData()
    {
        if (null != resultData)
        {
            return resultData.clone();
        }
        return new byte[]{};
    }
    
    public void setResultData(byte[] resultData)
    {
        if (null != resultData)
        {
            this.resultData = resultData.clone();
        }
        else
        {
            this.resultData = null;
        }
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public String getResultStr()
    {
        return resultStr;
    }
    
    public void setResultStr(String resultStr)
    {
        this.resultStr = resultStr;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}

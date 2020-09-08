package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import com.huawei.sharedrive.uam.util.PropertiesUtils;

public class ExcelImport implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final int IMPORT_SUCCESS = 1;
    
    public static final int IMPORT_FAILED = 2;
    
    public static final int IMPORTING = 3;
    
    public static final int MAX_IMPORT = Integer.parseInt(PropertiesUtils.getProperty("max.import.count", "5"));
    
    private String id;
    
    private byte[] errData;
    
    private Date runtime;
    
    private Date completeTime;
    
    private int status;
    
    private long enterpriseId;
    
    private long authServerId;
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setErrData(byte[] errData)
    {
        if (null != errData)
        {
            this.errData = errData.clone();
        }
        else
        {
            this.errData = ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
    
    public byte[] getErrData()
    {
        if (null != errData)
        {
            return errData.clone();
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
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
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setAuthServerId(long authServerId)
    {
        this.authServerId = authServerId;
    }
    
    public long getAuthServerId()
    {
        return authServerId;
    }
    
}

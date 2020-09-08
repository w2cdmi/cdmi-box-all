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
package com.huawei.sharedrive.isystem.cluster.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.utils.IpUtils;

/**
 * 网段
 * 
 * @author s90006125
 * 
 */
public class NetSegment implements Serializable
{
    private static final long serialVersionUID = -8958230864025042540L;
    
    private long id;
    
    private String startIp;
    
    private String endIp;
    
    private int regionId;
    
    public NetSegment()
    {
    }
    
    public NetSegment(String startIp, String endIp)
    {
        this.startIp = startIp;
        this.endIp = endIp;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getStartIp()
    {
        return startIp;
    }
    
    public void setStartIp(String startIp)
    {
        this.startIp = startIp;
    }
    
    public String getEndIp()
    {
        return endIp;
    }
    
    public void setEndIp(String endIp)
    {
        this.endIp = endIp;
    }
    
    public long getStart()
    {
        return IpUtils.toLong(this.startIp);
    }
    
    public long getEnd()
    {
        return IpUtils.toLong(this.endIp);
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public boolean validate()
    {
        if (StringUtils.isBlank(this.getStartIp()) || StringUtils.isBlank(this.endIp))
        {
            return false;
        }
        
        long start = this.getStart();
        if (start < 0)
        {
            return false;
        }
        long end = this.getEnd();
        if (end < 0)
        {
            return false;
        }
        if (end < start)
        {
            return false;
        }
        
        return true;
    }
}

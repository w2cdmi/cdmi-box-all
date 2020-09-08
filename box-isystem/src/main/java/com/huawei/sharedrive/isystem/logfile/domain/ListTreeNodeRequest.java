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
package com.huawei.sharedrive.isystem.logfile.domain;

/**
 * 
 * @author s90006125
 *
 */
public class ListTreeNodeRequest
{
    private Integer regionId;
    private String appId;
    private long clusterId;
    private Integer dssId;
    public Integer getRegionId()
    {
        return regionId;
    }
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    public String getAppId()
    {
        return appId;
    }
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    public long getClusterId()
    {
        return clusterId;
    }
    public void setClusterId(long clusterId)
    {
        this.clusterId = clusterId;
    }
    public Integer getDssId()
    {
        return dssId;
    }
    public void setDssId(Integer dssId)
    {
        this.dssId = dssId;
    }
}

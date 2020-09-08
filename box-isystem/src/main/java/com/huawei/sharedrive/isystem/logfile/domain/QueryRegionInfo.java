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
public class QueryRegionInfo
{
    private int clusterType;
    private Integer clusterId;
    private Integer regionId;
    public int getClusterType()
    {
        return clusterType;
    }
    public void setClusterType(int clusterType)
    {
        this.clusterType = clusterType;
    }
    public Integer getClusterId()
    {
        return clusterId;
    }
    public void setClusterId(Integer clusterId)
    {
        this.clusterId = clusterId;
    }
    public Integer getRegionId()
    {
        return regionId;
    }
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
}

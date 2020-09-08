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
package com.huawei.sharedrive.isystem.logfile.service.impl;

public class AddLogAgentRequest
{
    private String clusterAddress;
    
    private String protocol;
    
    private String serviceContextPath;
    
    private int clusterId;
    
    private int port;
    
    public String getClusterAddress()
    {
        return clusterAddress;
    }
    
    public String getProtocol()
    {
        return protocol;
    }
    
    public String getServiceContextPath()
    {
        return serviceContextPath;
    }
    
    public int getClusterId()
    {
        return clusterId;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setClusterAddress(String clusterAddress)
    {
        this.clusterAddress = clusterAddress;
    }
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }
    
    public void setServiceContextPath(String serviceContextPath)
    {
        this.serviceContextPath = serviceContextPath;
    }
    
    public void setClusterId(int clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
}

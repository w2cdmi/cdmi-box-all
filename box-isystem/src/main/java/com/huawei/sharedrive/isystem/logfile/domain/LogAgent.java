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

import java.util.Date;

/**
 * 
 * @author s90006125
 * 
 */
public class LogAgent
{
    public static final String DEFAULT_ADDRESS = "127.0.0.1";
    
    public static final int DEFAULT_CLUSTERID = -1;
    
    public static final int DEFAULT_PORT = 8445;
    
    public static final String DEFAULT_PROTOCOL = "https";
    
    public static final String DEFAULT_SK = "";
    
    private String address;
    
    // 集群的ID
    // 如果是AC，ID=-1
    // 如果是DC，ID=数据中心（data_center表）的ID
    private int clusterId;
    
    // 集群名称
    // 如果是AC，name=AC
    // 如果是DC，名称=数据中心（data_center表）的name
    private String clusterName;
    
    private Date createdAt;
    
    private int id = -1;
    
    private Date modifiedAt;
    
    private int port;
    
    private String protocol;
    
    private String serviceContextPath;
    
    public String getAccessURL()
    {
        return new StringBuilder(this.getProtocol()).append("://")
            .append(this.getAddress())
            .append(":")
            .append(this.getPort())
            .append("/")
            .append(this.getServiceContextPath())
            .toString();
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public int getClusterId()
    {
        return clusterId;
    }
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public int getId()
    {
        return id;
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public String getProtocol()
    {
        return protocol;
    }
    
    public String getServiceContextPath()
    {
        return serviceContextPath;
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public void setClusterId(int clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        this.modifiedAt = modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }
    
    public void setServiceContextPath(String serviceContextPath)
    {
        this.serviceContextPath = serviceContextPath;
    }
}

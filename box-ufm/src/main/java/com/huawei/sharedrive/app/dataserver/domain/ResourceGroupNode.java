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
package com.huawei.sharedrive.app.dataserver.domain;

import java.io.Serializable;

/**
 * 资源组集群节点
 * 
 * @author s90006125
 * 
 */
public class ResourceGroupNode implements Serializable
{
    public static enum RuntimeStatus
    {
        /**
         * 正常
         */
        Normal(0),
        
        /**
         * 离线
         */
        Offline(2),
        
        /**
         * 1：存储异常
         */
        StorageAbnormal(1);
        
        private int code;
        
        private RuntimeStatus(int code)
        {
            this.code = code;
        }
        
        public static RuntimeStatus parseStatus(int status)
        {
            int code = 0;
            for (RuntimeStatus s : RuntimeStatus.values())
            {
                code = s.getCode();
                if (status == code)
                {
                    return s;
                }
            }
            
            return null;
        }
        
        public int getCode()
        {
            return code;
        }
    }
    
    public static enum Status
    {
        Disable(0), Enable(1);
        
        private int code;
        
        private Status(int code)
        {
            this.code = code;
        }
        
        public static Status parseStatus(int status)
        {
            int code = 0;
            for (Status s : Status.values())
            {
                code = s.getCode();
                if (status == code)
                {
                    return s;
                }
            }
            
            return null;
        }
        
        public int getCode()
        {
            return code;
        }
    }
    
    private static final long serialVersionUID = -5552617473234541009L;
    
    private int dcId;
    
    private String innerAddr;
    
    private long lastReportTime;
    
    private String managerIp;
    
    private int managerPort;
    
    private String name;
    
    private String natAddr;
    
    private String natPath;
    
    private int priority = 1;
    
    private int regionId;
    
    private int resourceGroupId;
    
    private RuntimeStatus runtimeStatus = RuntimeStatus.Normal;
    
    private String serviceAddr;
    
    private Status status = Status.Enable;
    
    public ResourceGroupNode()
    {
    }
    
    public ResourceGroupNode(String name)
    {
        this();
        this.name = name;
    }
    
    public int getDcId()
    {
        return dcId;
    }
    
    public String getInnerAddr()
    {
        return innerAddr;
    }
    
    public long getLastReportTime()
    {
        return lastReportTime;
    }
    
    public String getManagerIp()
    {
        return managerIp;
    }
    
    public int getManagerPort()
    {
        return managerPort;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getNatAddr()
    {
        return natAddr;
    }
    
    public String getNatPath()
    {
        return natPath;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public RuntimeStatus getRuntimeStatus()
    {
        return runtimeStatus;
    }
    
    public String getServiceAddr()
    {
        return serviceAddr;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setDcId(int dcid)
    {
        this.dcId = dcid;
    }
    
    public void setInnerAddr(String innerAddr)
    {
        this.innerAddr = innerAddr;
    }
    
    public void setLastReportTime(long lastReportTime)
    {
        this.lastReportTime = lastReportTime;
    }
    
    public void setManagerIp(String managerIp)
    {
        this.managerIp = managerIp;
    }
    
    public void setManagerPort(int managerPort)
    {
        this.managerPort = managerPort;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNatAddr(String natAddr)
    {
        this.natAddr = natAddr;
    }
    
    public void setNatPath(String natPath)
    {
        this.natPath = natPath;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public void setRegionId(int regionID)
    {
        this.regionId = regionID;
    }
    
    public void setResourceGroupId(int resourceGroupID)
    {
        this.resourceGroupId = resourceGroupID;
    }
    
    public void setRuntimeStatus(RuntimeStatus runtimeStatus)
    {
        this.runtimeStatus = runtimeStatus;
    }
    
    public void setServiceAddr(String serviceAddr)
    {
        this.serviceAddr = serviceAddr;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
}

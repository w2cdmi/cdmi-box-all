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

/**
 * 资源组集群节点
 * 
 * @author s90006125
 * 
 */
public class ResourceGroupNode implements Serializable
{
    private static final long serialVersionUID = 6951833673034858567L;
    
    private String name;
    
    private int resourceGroupID;
    
    private int dcId;
    
    private int regionId;
    
    private String managerIp;
    
    private int managerPort;
    
    private String innerAddr;
    
    private String serviceAddr;
    
    private String natAddr;
    
    private String natPath;
    
    private Status status = Status.Enable;
    
    private RuntimeStatus runtimeStatus = RuntimeStatus.Normal;
    
    private long lastReportTime;
    
    private int priority = 1;
    
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

    public void setDcId(int dcId)
    {
        this.dcId = dcId;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getResourceGroupID()
    {
        return resourceGroupID;
    }
    
    public void setResourceGroupID(int resourceGroupID)
    {
        this.resourceGroupID = resourceGroupID;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public String getManagerIp()
    {
        return managerIp;
    }
    
    public void setManagerIp(String managerIp)
    {
        this.managerIp = managerIp;
    }
    
    public int getManagerPort()
    {
        return managerPort;
    }
    
    public void setManagerPort(int managerPort)
    {
        this.managerPort = managerPort;
    }
    
    public String getInnerAddr()
    {
        return innerAddr;
    }
    
    public void setInnerAddr(String innerAddr)
    {
        this.innerAddr = innerAddr;
    }
    
    public String getServiceAddr()
    {
        return serviceAddr;
    }
    
    public void setServiceAddr(String serviceAddr)
    {
        this.serviceAddr = serviceAddr;
    }
    
    public String getNatAddr()
    {
        return natAddr;
    }
    
    public void setNatAddr(String natAddr)
    {
        this.natAddr = natAddr;
    }
    
    public String getNatPath()
    {
        return natPath;
    }
    
    public void setNatPath(String natPath)
    {
        this.natPath = natPath;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public RuntimeStatus getRuntimeStatus()
    {
        return runtimeStatus;
    }
    
    public void setRuntimeStatus(RuntimeStatus runtimeStatus)
    {
        this.runtimeStatus = runtimeStatus;
    }
    
    public long getLastReportTime()
    {
        return lastReportTime;
    }
    
    public void setLastReportTime(long lastReportTime)
    {
        this.lastReportTime = lastReportTime;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public static enum Status
    {
        Disable(0), Enable(1);
        
        private int code;
        
        private Status(int code)
        {
            this.code = code;
        }
        
        public int getCode()
        {
            return code;
        }
        
        public static Status parseStatus(int inputCode)
        {
            int code = 0;
            for (Status s : Status.values())
            {
                code = s.getCode();
                if (inputCode == code)
                {
                    return s;
                }
            }
            
            return null;
        }
    }
    
    public static enum RuntimeStatus
    {
        /**
         * 正常
         */
        Normal(0),
        
        /**
         * 1：存储异常
         */
        StorageAbnormal(1),
        
        /**
         * 离线
         */
        Offline(2);
        
        private int code;
        
        private RuntimeStatus(int code)
        {
            this.code = code;
        }
        
        public int getCode()
        {
            return code;
        }
        
        public static RuntimeStatus parseStatus(int inputCode)
        {
            int code = 0;
            for (RuntimeStatus s : RuntimeStatus.values())
            {
                code = s.getCode();
                if (inputCode == code)
                {
                    return s;
                }
            }
            
            return null;
        }
    }
}

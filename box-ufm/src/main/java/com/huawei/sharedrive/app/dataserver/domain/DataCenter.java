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
 * DC 对象
 * 
 * @author s90006125
 * 
 */
public class DataCenter implements Serializable
{
    public static enum Status
    {
        /** 停用状态，暂不实现 */
        Disable(2),
        /** 已启用 */
        Enable(1),
        /** 初始状态 */
        Initial(0);
        
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
    
    private static final long serialVersionUID = 2633028615023648472L;
    
    private int id;
    
    private String name;
    
    private Integer priority; 
    
    private Region region = new Region();
    
    /** 当前版本dc和resourcegroup是1:1的关系 */
    private ResourceGroup resourceGroup = new ResourceGroup();
    
    private Status status = Status.Initial;
    
    public int getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Region getRegion()
    {
        return region;
    }
    
    public ResourceGroup getResourceGroup()
    {
        return resourceGroup;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRegion(Region region)
    {
        this.region = region;
    }
    
    public void setResourceGroup(ResourceGroup resourceGroup)
    {
        this.resourceGroup = resourceGroup;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }
}

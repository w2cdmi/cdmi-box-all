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
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;

/**
 * DC 对象
 * 
 * @author s90006125
 * 
 */
public class DataCenter implements Serializable
{
    private static final long serialVersionUID = 2633028615023648472L;
    
    private int id;
    
    @NotNull
    @Size(min = 3, max = 128)
    private String name;
 
    
    @JsonIgnore
    private Region region = new Region();
    
    @JsonIgnore
    private Status status = Status.Initial;
    
    private int priority;
    
    @JsonIgnore
    /** 当前版本dc和resourcegroup是1:1的关系 */
    private ResourceGroup resourceGroup = new ResourceGroup();
    
    private List<DssDomain> dssDomain = new ArrayList<DssDomain>(10);
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Region getRegion()
    {
        return region;
    }
    
    public void setRegion(Region region)
    {
        this.region = region;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public ResourceGroup getResourceGroup()
    {
        return resourceGroup;
    }
    
    public void setResourceGroup(ResourceGroup resourceGroup)
    {
        this.resourceGroup = resourceGroup;
    }
    
    public List<DssDomain> getDssDomain()
    {
        return dssDomain;
    }
    
    public void setDssDomain(List<DssDomain> dssDomain)
    {
        this.dssDomain = dssDomain;
    }
    
    public void setResourceGroupNodeRuntimeStatus(com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode.RuntimeStatus runtimeStatus)
    {
        resourceGroup.setNodesRuntimeStatus(runtimeStatus);
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
        /** 初始状态 */
        Initial(0),
        /** 已启用 */
        Enable(1),
        /** 停用状态，暂不实现 */
        Disable(2);
        
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
}

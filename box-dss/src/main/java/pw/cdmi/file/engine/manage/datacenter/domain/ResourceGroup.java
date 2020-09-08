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
package pw.cdmi.file.engine.manage.datacenter.domain;

import java.util.List;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.AutoToStringEntity;

/**
 * 资源组信息
 * 
 * @author s90006125
 * 
 */
public class ResourceGroup extends AutoToStringEntity implements LogFormat
{
    private static final long serialVersionUID = -8614023050367141859L;
    
    private int id;
    
    private int type;
    
    private String managerAddr;
    
    private int managerPort;
    
    private String getProtocol;
    
    private String putProtocol;
    
    private int serviceHttpPort;
    
    private int serviceHttpsPort;
    
    private String servicePath;
    
    private List<ClusterNode> nodes;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getManagerAddr()
    {
        return managerAddr;
    }
    
    public void setManagerAddr(String managerAddr)
    {
        this.managerAddr = managerAddr;
    }
    
    public int getManagerPort()
    {
        return managerPort;
    }
    
    public void setManagerPort(int managerPort)
    {
        this.managerPort = managerPort;
    }
    
    public String getGetProtocol()
    {
        return getProtocol;
    }

    public void setGetProtocol(String getProtocol)
    {
        this.getProtocol = getProtocol;
    }

    public String getPutProtocol()
    {
        return putProtocol;
    }

    public void setPutProtocol(String putProtocol)
    {
        this.putProtocol = putProtocol;
    }

    public int getServiceHttpPort()
    {
        return serviceHttpPort;
    }
    
    public void setServiceHttpPort(int serviceHttpPort)
    {
        this.serviceHttpPort = serviceHttpPort;
    }
    
    public int getServiceHttpsPort()
    {
        return serviceHttpsPort;
    }
    
    public void setServiceHttpsPort(int serviceHttpsPort)
    {
        this.serviceHttpsPort = serviceHttpsPort;
    }
    
    public String getServicePath()
    {
        return servicePath;
    }
    
    public void setServicePath(String servicePath)
    {
        this.servicePath = servicePath;
    }
    
    public List<ClusterNode> getNodes()
    {
        return nodes;
    }
    
    public void setNodes(List<ClusterNode> nodes)
    {
        this.nodes = nodes;
    }
    
    @Override
    public String logFormat()
    {
        return this.toString();
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}

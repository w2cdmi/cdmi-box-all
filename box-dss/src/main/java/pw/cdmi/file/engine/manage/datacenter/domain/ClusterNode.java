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

import pw.cdmi.file.engine.core.common.AutoToStringEntity;
import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * 集群节点
 * 
 * @author s90006125
 * 
 */
@Namingspace("clusterNode")
public class ClusterNode extends AutoToStringEntity
{
    private static final long serialVersionUID = 302065157799160409L;
    
    private String name;
    
    /** 该网段是管理网的网段地址 */
    private String manageAddr;
    
    /** 网络整改后废弃该字段 */
    @Deprecated
    private String managerGateway;
    
    /** 该端口是指AC和DC之间通信的管理端口，而不是管理网网段端口 */
    private int managePort;
    
    /** 私网 */
    private String innerAddr;
    
    private String innerGateway;
    
    /** 公网 */
    private String serviceAddr;
    
    private String serviceGateway;
    
    private NodeStatus status = NodeStatus.Enable;
    
    private RuntimeStatus runtimeStatus = RuntimeStatus.Normal;
    
    private long lastReportTime;
    
    public ClusterNode()
    {
    }
    
    public ClusterNode(String name)
    {
        this();
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getManageAddr()
    {
        return manageAddr;
    }
    
    public void setManageAddr(String managerAddr)
    {
        this.manageAddr = managerAddr;
    }
    
    public int getManagePort()
    {
        return managePort;
    }
    
    public void setManagePort(int managePort)
    {
        this.managePort = managePort;
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
    
    public NodeStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(NodeStatus status)
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
    
    @Deprecated
    public String getManagerGateway()
    {
        return managerGateway;
    }
    
    @Deprecated
    public void setManagerGateway(String managerGateway)
    {
        this.managerGateway = managerGateway;
    }
    
    public String getInnerGateway()
    {
        return innerGateway;
    }
    
    public void setInnerGateway(String innerGateway)
    {
        this.innerGateway = innerGateway;
    }
    
    public String getServiceGateway()
    {
        return serviceGateway;
    }
    
    public void setServiceGateway(String serviceGateway)
    {
        this.serviceGateway = serviceGateway;
    }
}

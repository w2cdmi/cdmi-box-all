/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.thrift.dns.Node;

/**
 * 资源组
 * 
 * @author q90003805
 * 
 */
public class ResourceGroup implements Serializable
{
    private static final long serialVersionUID = 4645121478805091839L;
    
    private int id;
    
    private int dcId;
    
    private int type;
    
    private int regionId;
    
    @NotNull
    @Size(min = 1, max = 45)
    private String manageIp;
    
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private int managePort;
    
    private int serviceHttpPort;
    
    private int serviceHttpsPort;
    
    private String servicePath;
    
    /** 域名：来自界面 */
    @Size(min = 0, max = 128)
    private String domainName;
    
    private String protocol;
    
    private Status status;
    
    private RWStatus rwStatus;
    
    private RuntimeStatus runtimeStatus;
    
    private long lastReportTime;
    
    private String accessKey;
    
    private List<ResourceGroupNode> nodes = new ArrayList<ResourceGroupNode>(10);
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getDcId()
    {
        return dcId;
    }
    
    public void setDcId(int dcId)
    {
        this.dcId = dcId;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public String getManageIp()
    {
        return manageIp;
    }
    
    public void setManageIp(String manageIp)
    {
        this.manageIp = manageIp;
    }
    
    public int getManagePort()
    {
        return managePort;
    }
    
    public void setManagePort(int managePort)
    {
        this.managePort = managePort;
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
    
    public String getDomainName()
    {
        return domainName;
    }
    
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public RWStatus getRwStatus()
    {
        return rwStatus;
    }

    public void setRwStatus(RWStatus rwStatus)
    {
        this.rwStatus = rwStatus;
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
    
    public String getAccessKey()
    {
        return accessKey;
    }
    
    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }
    
    public List<ResourceGroupNode> getNodes()
    {
        return nodes;
    }
    
    public void setNodes(List<ResourceGroupNode> nodes)
    {
        this.nodes = nodes;
    }
    
    public void addNode(ResourceGroupNode node)
    {
        if (null == nodes)
        {
            this.nodes = new ArrayList<ResourceGroupNode>(10);
        }
        this.nodes.add(node);
    }
    
    public String getProtocol()
    {
        return protocol;
    }
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void setNodesRuntimeStatus(
        com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode.RuntimeStatus runtimeStatus)
    {
        for (ResourceGroupNode node : nodes)
        {
            node.setRuntimeStatus(runtimeStatus);
        }
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
    
    public static enum RWStatus
    {
        Normal(0),
        ReadOnly(1);
        private int code;
        private RWStatus(int code)
        {
            this.code = code;
        }
        
        public static RWStatus parseStatus(int status)
        {
            int code = 0;
            for(RWStatus s : RWStatus.values())
            {
                code = s.getCode();
                if(status == code)
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
    
    public static enum RuntimeStatus
    {
        /**
         * 正常
         */
        Normal(0),
        
        /**
         * 有异常，其中一个节点有问题，就显示该状态
         */
        Abnormal(1),
        
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
    
    public void adjustStatus(Map<String, Node> dnsNodesMap)
    {
        Node tempNode = null;
        for (ResourceGroupNode node : nodes)
        {
            tempNode = dnsNodesMap.get(node.getServiceAddr());
            if (tempNode != null && tempNode.isAvailable())
            {
                node.setRuntimeStatus(com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode.RuntimeStatus.Normal);
            }
            else
            {
                node.setRuntimeStatus(com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode.RuntimeStatus.Offline);
            }
        }
    }
}

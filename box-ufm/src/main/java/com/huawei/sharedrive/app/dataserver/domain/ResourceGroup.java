/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 资源组
 * 
 * @author q90003805
 * 
 */
public class ResourceGroup implements Serializable
{
    public static enum RuntimeStatus
    {
        /**
         * 有异常，其中一个节点有问题，就显示该状态
         */
        Abnormal(1),
        
        /**
         * 正常
         */
        Normal(0),
        
        /**
         * 离线
         */
        Offline(2);
        
        private int code;
        
        private RuntimeStatus(int codeTemp)
        {
            this.code = codeTemp;
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
        /** 已启用 */
        Enable(1),
        /** 初始状态 */
        Initial(0),
        /** 停用状态，暂不实现 */
        Disable(2);
        
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
    
    public static enum Type
    {
        /** 合并部署DSS */
        Merge(0),
        
        /** 分开部署DSS */
        Distribute(1);
        
        private int code;
        
        private Type(int code)
        {
            this.code = code;
        }
        
        public static Type getType(int code)
        {
            for (Type type : Type.values())
            {
                if (type.getCode() == code)
                {
                    return type;
                }
            }
            throw new InvalidParamException("Invalid resource group code " + code);
        }
        
        public int getCode()
        {
            return code;
        }
    }
    private static final long serialVersionUID = 4645121478805091839L;
    
    private String accessKey;
    
    private int dcId;
    
    /** 域名：来自界面 */
    private String domainName;
    
    private int id;
    
    private int type;
    
    private long lastReportTime;
    
    private String manageIp;
    
    private int managePort;
    
    private String getProtocol;
    
    private String putProtocol;
    
    private List<ResourceGroupNode> nodes = new ArrayList<ResourceGroupNode>(BusinessConstants.INITIAL_CAPACITIES);
    
    private int regionId;
    
    private RuntimeStatus runtimeStatus;
    
    private int serviceHttpPort;
    
    private int serviceHttpsPort;
    
    private String servicePath;
    
    private Status status;
    
    private RWStatus rwStatus;
    
    public void addNode(ResourceGroupNode node)
    {
        if (null == nodes)
        {
            this.nodes = new ArrayList<ResourceGroupNode>(BusinessConstants.INITIAL_CAPACITIES);
        }
        this.nodes.add(node);
    }
    
    public String getAccessKey()
    {
        return accessKey;
    }
    
    public int getDcId()
    {
        return dcId;
    }
    
    public String getDomainName()
    {
        return domainName;
    }
    
    public int getId()
    {
        return id;
    }
    
    public long getLastReportTime()
    {
        return lastReportTime;
    }
    
    public String getManageIp()
    {
        return manageIp;
    }
    
    public int getManagePort()
    {
        return managePort;
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

    public List<ResourceGroupNode> getNodes()
    {
        return nodes;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public RuntimeStatus getRuntimeStatus()
    {
        return runtimeStatus;
    }
    
    public int getServiceHttpPort()
    {
        return serviceHttpPort;
    }
    
    public int getServiceHttpsPort()
    {
        return serviceHttpsPort;
    }
    
    public String getServicePath()
    {
        return servicePath;
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

    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }
    
    public void setDcId(int dcId)
    {
        this.dcId = dcId;
    }
    
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setLastReportTime(long lastReportTime)
    {
        this.lastReportTime = lastReportTime;
    }
    
    public void setManageIp(String manageIp)
    {
        this.manageIp = manageIp;
    }
    
    public void setManagePort(int managePort)
    {
        this.managePort = managePort;
    }
    
    public void setNodes(List<ResourceGroupNode> nodes)
    {
        this.nodes = nodes;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public void setRuntimeStatus(RuntimeStatus runtimeStatus)
    {
        this.runtimeStatus = runtimeStatus;
    }
    
    public void setServiceHttpPort(int serviceHttpPort)
    {
        this.serviceHttpPort = serviceHttpPort;
    }
    
    public void setServiceHttpsPort(int serviceHttpsPort)
    {
        this.serviceHttpsPort = serviceHttpsPort;
    }
    
    public void setServicePath(String servicePath)
    {
        this.servicePath = servicePath;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
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

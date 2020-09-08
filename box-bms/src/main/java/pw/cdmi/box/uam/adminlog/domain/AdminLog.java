package pw.cdmi.box.uam.adminlog.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.user.domain.Admin;

public class AdminLog
{
    private long id;
    
    private String logid;
    
    private Admin adminInfo = new Admin();
    
    private OperateType operateType;
    
    private String operatrDetails;
    
    private Date createAt;
    
    private Boolean success;
    
    private String description;
    
    private String clientAddress;
    
    private String serverName;
    
    private String beforeOper;
    
    private String afterOper;
    
    private String appId;
    
    public String getOperatrDetails()
    {
        return operatrDetails;
    }
    
    public void setOperatrDetails(String operatrDetails)
    {
        this.operatrDetails = operatrDetails;
    }
    
    public long getId()
    {
        return id;
    }
    
    public AdminLog setId(long id)
    {
        this.id = id;
        return this;
    }
    
    public String getLogid()
    {
        return logid;
    }
    
    public AdminLog setLogid(String logid)
    {
        this.logid = logid;
        return this;
    }
    
    public Admin getAdminInfo()
    {
        return adminInfo;
    }
    
    public void setAdminInfo(Admin adminInfo)
    {
        this.adminInfo = adminInfo;
    }
    
    public OperateType getOperateType()
    {
        return operateType;
    }
    
    public AdminLog setOperateType(OperateType operateType)
    {
        this.operateType = operateType;
        return this;
    }
    
    public Date getCreateAt()
    {
        return createAt == null ? null : (Date) createAt.clone();
    }
    
    public AdminLog setCreateAt(Date createAt)
    {
        this.createAt = (createAt == null ? null : (Date) createAt.clone());
        return this;
    }
    
    public Boolean isSuccess()
    {
        return success;
    }
    
    public Boolean getSuccess()
    {
        return this.success;
    }
    
    public AdminLog setSuccess(Boolean success)
    {
        this.success = success;
        return this;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public AdminLog setDescription(String description)
    {
        this.description = description;
        return this;
    }
    
    public String getClientAddress()
    {
        return clientAddress;
    }
    
    public AdminLog setClientAddress(String clientAddress)
    {
        this.clientAddress = clientAddress;
        return this;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public AdminLog setServerName(String serverName)
    {
        this.serverName = serverName;
        return this;
    }
    
    public String getBeforeOper()
    {
        return beforeOper;
    }
    
    public AdminLog setBeforeOper(String beforeOper)
    {
        this.beforeOper = beforeOper;
        return this;
    }
    
    public String getAfterOper()
    {
        return afterOper;
    }
    
    public AdminLog setAfterOper(String afterOper)
    {
        this.afterOper = afterOper;
        return this;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public AdminLog fillOperatorInfo(Admin admin)
    {
        if (StringUtils.isNotBlank(this.getAdminInfo().getLoginName()))
        {
            return this;
        }
        this.setAdminInfo(admin);
        return this;
    }
    
    public AdminLog fillLogid(String logid)
    {
        if (StringUtils.isNotBlank(this.getLogid()))
        {
            return this;
        }
        this.setLogid(logid);
        return this;
    }
    
    public AdminLog fillOperateType(OperateType operateType)
    {
        if (null != this.getOperateType())
        {
            return this;
        }
        this.setOperateType(operateType);
        return this;
    }
    
    public AdminLog fillAdminInfo(Admin adminInfo)
    {
        if (StringUtils.isEmpty(this.getAdminInfo().getName()))
        {
            this.getAdminInfo().setName(adminInfo.getName());
        }
        if (StringUtils.isEmpty(this.getAdminInfo().getLoginName()))
        {
            this.getAdminInfo().setLoginName(adminInfo.getLoginName());
        }
        return this;
    }
    
    public AdminLog fillCreateAt(Date createAt)
    {
        if (null != this.getCreateAt())
        {
            return this;
        }
        this.setCreateAt(createAt);
        return this;
    }
    
    public AdminLog fillSuccess(boolean success)
    {
        if (null != this.isSuccess())
        {
            return this;
        }
        this.setSuccess(success);
        return this;
    }
    
    public AdminLog fillDescription(String description)
    {
        if (StringUtils.isNotBlank(this.getDescription()))
        {
            return this;
        }
        this.setDescription(description);
        return this;
    }
    
    public AdminLog fillAppId(String appId)
    {
        if (StringUtils.isNotBlank(this.getAppId()))
        {
            return this;
        }
        this.setAppId(appId);
        return this;
    }
    
    public AdminLog fillClientAddress(String clientAddress)
    {
        if (StringUtils.isNotBlank(this.getClientAddress()))
        {
            return this;
        }
        this.setClientAddress(clientAddress);
        return this;
    }
    
    public AdminLog fillServerName(String serverName)
    {
        if (StringUtils.isNotBlank(this.getServerName()))
        {
            return this;
        }
        this.setServerName(serverName);
        return this;
    }
    
    public AdminLog fillBeforeOper(String beforeOper)
    {
        if (StringUtils.isNotBlank(this.getBeforeOper()))
        {
            return this;
        }
        this.setBeforeOper(beforeOper);
        return this;
    }
    
    public AdminLog fillAfterOper(String afterOper)
    {
        if (StringUtils.isNotBlank(this.getAfterOper()))
        {
            return this;
        }
        this.setAfterOper(afterOper);
        return this;
    }
    
}

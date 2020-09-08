/**
 * 
 */
package pw.cdmi.box.uam.enterpriseradminlog.domain;

import java.io.Serializable;
import java.util.Date;

public class EnterpriseAdminLog implements Serializable
{
    
    private static final long serialVersionUID = 4403631163038368515L;
    
    private String id;
    
    private Long enterpriseId;
    
    private Date createTime;
    
    private String loginName;
    
    private Integer level;
    
    private String ip;
    
    private String appId;
    
    private Integer operatDescKey;
    
    private String operatDesc;
    
    private Long authserverId;
    
    private String operatType;
    
    private String operatLevel;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public Date getCreateTime()
    {
        return createTime == null ? null : (Date) createTime.clone();
    }
    
    public void setCreateTime(Date createTime)
    {
        this.createTime = (createTime == null ? null : (Date) createTime.clone());
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public Integer getLevel()
    {
        return level;
    }
    
    public void setLevel(Integer level)
    {
        this.level = level;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Integer getOperatDescKey()
    {
        return operatDescKey;
    }
    
    public void setOperatDescKey(Integer operatDescKey)
    {
        this.operatDescKey = operatDescKey;
    }
    
    public String getOperatDesc()
    {
        return operatDesc;
    }
    
    public void setOperatDesc(String operatDesc)
    {
        this.operatDesc = operatDesc;
    }
    
    public Long getAuthserverId()
    {
        return authserverId;
    }
    
    public void setAuthserverId(Long authserverId)
    {
        this.authserverId = authserverId;
    }
    
    public String getOperatType()
    {
        return operatType;
    }
    
    public void setOperatType(String operatType)
    {
        this.operatType = operatType;
    }
    
    public String getOperatLevel()
    {
        return operatLevel;
    }
    
    public void setOperatLevel(String operatLevel)
    {
        this.operatLevel = operatLevel;
    }
    
}

package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;


public class RestLinkCreateRequestV2
{
    
    private String access;
    
    private long effectiveAt;
    
    private long expireAt;
    
    private String plainAccessCode;
    
    private List<LinkIdentityInfo> identities;
    
    private String role;
    
    private String accessCodeMode;
    
    private boolean needLogin;
    
    private String linkName;
    
    private List<INode> nodeList;
    
    //不能转存
    private Boolean disdump;
    
    private Boolean isProgram;

	public String getAccess()
    {
        return access;
    }
    
    
    public long getEffectiveAt()
    {
        return effectiveAt;
    }


    public long getExpireAt()
    {
        return expireAt;
    }


    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }


    public void setAccess(String access)
    {
        this.access = access;
    }


    public void setEffectiveAt(long effectiveAt)
    {
        this.effectiveAt = effectiveAt;
    }


    public void setExpireAt(long expireAt)
    {
        this.expireAt = expireAt;
    }


    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }


    public String getAccessCodeMode()
    {
        return accessCodeMode;
    }


    public void setAccessCodeMode(String accessCodeMode)
    {
        this.accessCodeMode = accessCodeMode;
    }


    public List<LinkIdentityInfo> getIdentities()
    {
        return identities;
    }


    public void setIdentities(List<LinkIdentityInfo> identities)
    {
        this.identities = identities;
    }


    public String getRole()
    {
        return role;
    }


    public void setRole(String role)
    {
        this.role = role;
    }


	public List<INode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<INode> nodeList) {
		this.nodeList = nodeList;
	}


	public boolean isNeedLogin() {
		return needLogin;
	}


	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}


	public String getLinkName() {
		return linkName;
	}


	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}


	public Boolean getDisdump() {
		return disdump;
	}


	public void setDisdump(Boolean disdump) {
		this.disdump = disdump;
	}


	public Boolean getIsProgram() {
		return isProgram;
	}


	public void setIsProgram(Boolean isProgram) {
		this.isProgram = isProgram;
	}


    
    
}

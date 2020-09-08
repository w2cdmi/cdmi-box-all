package pw.cdmi.box.disk.share.domain;

import java.util.List;

public class RestLinkCreateRequestV2
{
    
    private long effectiveAt;
    
    private long expireAt;
    
    private String plainAccessCode;
    
    private String role;
    
    private String accessCodeMode;
    
    private boolean needLogin;
    
    private List<LinkIdentityInfo> identities;
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
    public long getEffectiveAt()
    {
        return effectiveAt;
    }
    
    public void setEffectiveAt(long effectiveAt)
    {
        this.effectiveAt = effectiveAt;
    }
    
    public long getExpireAt()
    {
        return expireAt;
    }
    
    public void setExpireAt(long expireAt)
    {
        this.expireAt = expireAt;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public void setRole(String role)
    {
        this.role = role;
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

	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}


    
    
}

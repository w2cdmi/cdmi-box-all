package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pw.cdmi.box.disk.client.domain.node.INode;

public class INodeLink implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -6063510834227830188L;

    public final static byte ACCESS_ANONYMOUS_TYPE = 0;
    
    public final static byte ACCESS_SHARE_TYPE = 2;
    
    public final static byte ACCESS_USER_TYPE = 1;
    
    public final static byte LINK_STATUS = 1;
    
    public final static byte NOT_LINK_STATUS = 0;
    
    private String access;
    
    private long createdBy;
    
    @JsonIgnore
    private String downloadUrl;
    
    private Date effectiveAt;
    
    private Date expireAt;
    
    private String id;
    
    private Long iNodeId;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private long ownedBy;
    
    @JsonIgnore
    private String password;
    
    private String plainAccessCode;
    
    private String role;
    
    private String url;
    
    private String creator;
    
    private String accessCodeMode;

    private String subINodes;
    
    private List<INode> subFileList;

    
    /**
     * 是否登录
     */
    private boolean needLogin;


    private List<LinkIdentityInfo> identities;
    
    public String getAccess()
    {
        return access;
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setAccess(String access)
    {
        this.access = access;
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getCreator()
    {
        return creator;
    }
    
    public void setCreator(String creator)
    {
        this.creator = creator;
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

    public Long getiNodeId() {
        return iNodeId;
    }

    public void setiNodeId(Long iNodeId) {
        this.iNodeId = iNodeId;
    }

    public String getSubINodes() {
        return subINodes;
    }

    public void setSubINodes(String subINodes) {
        this.subINodes = subINodes;
    }

    public Date getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(Date effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

	public List<INode> getSubFileList() {
		return subFileList;
	}

	public void setSubFileList(List<INode> subFileList) {
		subFileList = subFileList;
	}
    
    
}

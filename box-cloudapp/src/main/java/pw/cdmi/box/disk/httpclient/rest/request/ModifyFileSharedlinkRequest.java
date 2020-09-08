package pw.cdmi.box.disk.httpclient.rest.request;

import pw.cdmi.box.disk.httpclient.rest.response.Permission;

public class ModifyFileSharedlinkRequest
{
    private String access = "open";
    
    private String password;
    
    private String effectiveAt;
    
    private String expireAt;
    
    private Permission permissions;
    
    private int previewCount;
    
    public String getAccess()
    {
        return access;
    }
    
    public void setAccess(String access)
    {
        this.access = access;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getEffectiveAt()
    {
        return effectiveAt;
    }
    
    public void setEffectiveAt(String effectiveAt)
    {
        this.effectiveAt = effectiveAt;
    }
    
    public String getExpireAt()
    {
        return expireAt;
    }
    
    public void setExpireAt(String expireAt)
    {
        this.expireAt = expireAt;
    }
    
    public Permission getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(Permission permissions)
    {
        this.permissions = permissions;
    }
    
    public int getPreviewCount()
    {
        return previewCount;
    }
    
    public void setPreviewCount(int previewCount)
    {
        this.previewCount = previewCount;
    }
    
}

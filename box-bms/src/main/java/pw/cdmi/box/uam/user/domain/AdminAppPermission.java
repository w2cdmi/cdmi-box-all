package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;

public class AdminAppPermission implements Serializable
{
    
    private static final long serialVersionUID = 7911191531309839572L;
    
    private long adminId;
    
    private String appId;
    
    public long getAdminId()
    {
        return adminId;
    }
    
    public void setAdminId(long adminId)
    {
        this.adminId = adminId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
}

package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;

public class LinkIdentityInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -5163031098035152872L;
    private String identity;
    
    public LinkIdentityInfo()
    {
        
    }
    
    public LinkIdentityInfo(String identity)
    {
        this.identity = identity;
    }
    
    public String getIdentity()
    {
        return identity;
    }
    
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }
    
}

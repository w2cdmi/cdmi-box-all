package pw.cdmi.box.uam.accountrole.domain;

public class AccountRole
{
    private String resourceRole;
    
    private long accountId;
    
    public String getResourceRole()
    {
        return resourceRole;
    }
    
    public void setResourceRole(String resourceRole)
    {
        this.resourceRole = resourceRole;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}

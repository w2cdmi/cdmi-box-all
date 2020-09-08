package pw.cdmi.box.uam.system.domain;

import java.util.List;

public class LdapUser
{
    
    private String baseDn;
    
    private List<String> memberOf;
    
    public void setBaseDn(String baseDn)
    {
        this.baseDn = baseDn;
    }
    
    public String getBaseDn()
    {
        return baseDn;
    }
    
    public void setMemberOf(List<String> memberOf)
    {
        this.memberOf = memberOf;
    }
    
    public List<String> getMemberOf()
    {
        return memberOf;
    }
    
}

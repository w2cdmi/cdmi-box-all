package pw.cdmi.box.uam.user.domain;

import java.util.List;

public class LdapFilterList
{
    private int totalNum;
    
    private List<LdapFilter> filterList;
    
    public int getTotalNum()
    {
        return totalNum;
    }
    
    public void setTotalNum(int totalNum)
    {
        this.totalNum = totalNum;
    }
    
    public List<LdapFilter> getLdapFilterList()
    {
        return filterList;
    }
    
    public void setLdapFilterList(List<LdapFilter> ldapFilterList)
    {
        this.filterList = ldapFilterList;
    }
    
}

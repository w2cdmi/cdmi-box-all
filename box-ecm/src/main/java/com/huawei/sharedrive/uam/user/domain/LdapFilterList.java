package com.huawei.sharedrive.uam.user.domain;

import java.util.List;

public class LdapFilterList
{
    private int totalNum;
    
    private List<LdapFilter> ldapFilter;
    
    public int getTotalNum()
    {
        return totalNum;
    }
    
    public void setTotalNum(int totalNum)
    {
        this.totalNum = totalNum;
    }
    
    public List<LdapFilter> getLdapFilter()
    {
        return ldapFilter;
    }
    
    public void setLdapFilter(List<LdapFilter> ldapFilter)
    {
        this.ldapFilter = ldapFilter;
    }
    
}

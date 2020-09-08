package com.huawei.sharedrive.uam.accountuser.domain;

import java.util.List;

import org.springframework.web.util.HtmlUtils;

public class UserAccountExtend extends UserAccount
{
    private static final long serialVersionUID = -7669563381664438258L;
    private String roleName;
    
    public static void htmlEscape(List<UserAccountExtend> list)
    {
        if (null == list)
        {
            return;
        }
        
        for (UserAccountExtend iter : list)
        {
            iter.setName(HtmlUtils.htmlEscape(iter.getName()));
            iter.setAlias(HtmlUtils.htmlEscape(iter.getAlias()));
            iter.setEmail(HtmlUtils.htmlEscape(iter.getEmail()));
            iter.setRoleName(HtmlUtils.htmlEscape(iter.getRoleName()));
        }
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
}

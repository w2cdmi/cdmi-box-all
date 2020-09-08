package pw.cdmi.box.disk.share.web;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.oauth2.domain.UserToken;

public class AdUserSortor implements Comparator<UserToken>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 5851561478359318030L;
    
    private String userDepart;
    
    public AdUserSortor(String userDepart)
    {
        this.userDepart = userDepart;
    }
    
    @Override
    public int compare(UserToken o1, UserToken o2)
    {
        if (StringUtils.equals(o1.getDepartment(), o2.getDepartment()))
        {
            return o1.getLoginName().compareTo(o2.getLoginName());
        }
        else if (StringUtils.equals(o1.getDepartment(), userDepart))
        {
            return -1;
        }
        else if (StringUtils.equals(o2.getDepartment(), userDepart))
        {
            return 1;
        }
        return o1.getLoginName().compareTo(o2.getLoginName());
    }
    
}

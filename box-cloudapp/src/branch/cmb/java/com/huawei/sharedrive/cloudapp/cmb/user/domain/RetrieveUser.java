package com.huawei.sharedrive.cloudapp.cmb.user.domain;

import java.io.Serializable;

import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.cloudapp.group.domain.GroupConstants;
import com.huawei.sharedrive.cloudapp.group.domain.RestGroup;
import com.huawei.sharedrive.cloudapp.share.domain.RestMultiUser;

public class RetrieveUser implements Serializable
{
    private static final long serialVersionUID = -9130775931582601596L;
    
    private Long cloudUserId;
    
    private String description;
    
    private String email;
    
    private Long id;
    
    private String loginName;
    
    private String name;
    
    private String status;
    
    private byte userType=0;
    
    private String label;
    
    public static RestMultiUser buildObject(RetrieveUser user, RestGroup group, byte userType)
    {
        RestMultiUser multUser = new RestMultiUser();
        if (userType == GroupConstants.GROUP_USERTYPE_USER)
        {
            multUser.setDepartment(HtmlUtils.htmlEscape(user.getDescription()));
            multUser.setEmail(user.getEmail());
            multUser.setId(user.getId());
            multUser.setCloudUserId(user.getCloudUserId());
            multUser.setLoginName(HtmlUtils.htmlEscape(user.getLoginName()));
            multUser.setName(HtmlUtils.htmlEscape(user.getName()));
            multUser.setStatus(user.getStatus());
            multUser.setLabel(HtmlUtils.htmlEscape(user.getName()));
            
        }
        else if (userType == GroupConstants.GROUP_USERTYPE_GROUP)
        {
            multUser.setId(group.getId());
            multUser.setCloudUserId(group.getId());
            multUser.setLoginName(HtmlUtils.htmlEscape(group.getName()));
            multUser.setDepartment(HtmlUtils.htmlEscape(group.getDescription()));
            multUser.setName(HtmlUtils.htmlEscape(group.getName()));
            multUser.setLabel(HtmlUtils.htmlEscape(group.getName()));
            multUser.setEmail(HtmlUtils.htmlEscape(group.getId() + GroupConstants.GROUP_EMAIL));
        }
        multUser.setUserType(userType);
        return multUser;
    }
    
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public byte getUserType()
    {
        return userType;
    }
    
    public void setUserType(byte userType)
    {
        this.userType = userType;
    }
    
}

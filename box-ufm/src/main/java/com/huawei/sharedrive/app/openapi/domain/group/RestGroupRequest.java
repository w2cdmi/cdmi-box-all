package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestGroupRequest
{
    /** 群组名称正则表达式 */
    private static final Pattern PATTERN_GROUP_NAME = Pattern.compile("[^.?!#/\\\\<>%?'\"&,;]{2,255}");
    
    private String description;
    
    private Integer maxMembers;
    
    private String name;
    
    private Long ownedBy;
    
    private String status;
    
    private String type;
    
    public void checkCreateParameter()
    {
        if (ownedBy != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownedBy);
        }
        checkName();
        checkMaxMembers();
        checkStatus();
        checkType();
        checkDespription();
    }

    private void checkName()
    {
        if(!isFormatName(name))
        {
            throw new InvalidParamException("Invalid name: " + name);
        }
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Integer getMaxMembers()
    {
        return maxMembers;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setMaxMembers(Integer maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    private void checkDespription()
    {
        if (null != this.getDescription())
        {
            this.setDescription(this.getDescription().trim());
            if (this.getDescription().length() > 1023)
            {
                throw new InvalidParamException("error description");
            }
        }
    }
    
    private void checkMaxMembers()
    {
        if (maxMembers == null)
        {
            maxMembers = GroupConstants.MAXMEMBERS_DEFAULT;
        }
        if (maxMembers.intValue() != -1)
        {
            FilesCommonUtils.checkNonNegativeIntegers(maxMembers);
        }
    }
    
    private void checkStatus()
    {
        if (status == null)
        {
            status = GroupConstants.STATUS_ENABLE;
        }
        if (!(StringUtils.equals(GroupConstants.STATUS_DISABLE, status) || StringUtils.equals(GroupConstants.STATUS_ENABLE,
            status)))
        {
            String errorMsg = "error status :" + status;
            throw new InvalidParamException(errorMsg);
        }
    }
    
    private void checkType()
    {
        if (type == null)
        {
            type = GroupConstants.TYPE_PRIVATE;
        }
        if (!(StringUtils.equals(GroupConstants.TYPE_PRIVATE, type) || StringUtils.equals(GroupConstants.TYPE_PUBLIC,
            type)))
        {
            String errorMsg = "error type :" + type;
            throw new InvalidParamException(errorMsg);
        }
    }
    
    private boolean isFormatName(String name)
    {
        if (name == null)
        {
            throw new InvalidParamException("Invalid name. ");
        }
        Matcher m = PATTERN_GROUP_NAME.matcher(name.trim());
        return m.matches();
    }
}

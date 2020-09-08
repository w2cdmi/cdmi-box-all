package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestGroupModifyRequest
{
    /** 群组名称正则表达式 */
    private static final Pattern PATTERN_GROUP_NAME = Pattern.compile("[^.?!#/\\\\<>%?'\"&,;]{2,255}");
    
    private String description;
    
    private Integer maxMembers;
    
    private String name;
    
    private String type;
    
    public void checkModifyParameter()
    {
        checkName();
        checkDespription();
        if (maxMembers != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(maxMembers);
        }
        if (type != null
            && !(StringUtils.equals(GroupConstants.TYPE_PRIVATE, type) || StringUtils.equals(GroupConstants.TYPE_PUBLIC,
                type)))
        {
            String errorMsg = "error type :" + type;
            throw new InvalidParamException(errorMsg);
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
    
    private void checkName()
    {
        if (name != null && !isFormatName(name))
        {
            throw new InvalidParamException("Invalid name: " + name);
        }
    }
    
    private boolean isFormatName(String name)
    {
        Matcher m = PATTERN_GROUP_NAME.matcher(name.trim());
        return m.matches();
    }
}

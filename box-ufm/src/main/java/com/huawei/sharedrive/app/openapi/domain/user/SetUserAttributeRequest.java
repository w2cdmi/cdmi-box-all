package com.huawei.sharedrive.app.openapi.domain.user;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

/**
 * 设置用户配置信息请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-31
 * @see
 * @since
 */
public class SetUserAttributeRequest
{
    // 启用消息通知
    private static final String MESSAGE_NOTICE_ENABLE = "enable";
    
    // 禁用消息通知
    private static final String MESSAGE_NOTICE_DISABLE = "disable";
    
    private String name;
    
    private String value;
    
    public void checkParameter()
    {
        if (StringUtils.isBlank(name))
        {
            throw new InvalidParamException("Invalid config name " + name);
        }
        if (StringUtils.isBlank(value))
        {
            throw new InvalidParamException("Invalid config value " + value);
        }
        
        UserAttribute attribute = UserAttribute.getUserAttribute(name);
        if (attribute == null)
        {
            throw new InvalidParamException("Invalid attribute " + name);
        }
        
        if (attribute == UserAttribute.MESSAGE_NOTICE)
        {
            if (!MESSAGE_NOTICE_DISABLE.equals(value) && !MESSAGE_NOTICE_ENABLE.equals(value))
            {
                throw new InvalidParamException("Invalid attribute value " + value);
            }
        }
        else 
        {
            throw new InvalidParamException("Invalid attribute " + name);
        }
        
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}

package com.huawei.sharedrive.app.openapi.domain.user;

/**
 * 团队空间配置项枚举类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum UserAttribute
{
    /** 消息通知 */
    MESSAGE_NOTICE("messageNotice");
    
    private String name;
    
    private UserAttribute(String name)
    {
        this.name = name;
    }
    
    public static UserAttribute getUserAttribute(String name)
    {
        for (UserAttribute attribute : UserAttribute.values())
        {
            if (attribute.getName().equals(name))
            {
                return attribute;
            }
        }
        return null;
    }
    
    public String getName()
    {
        return name;
    }
    
}

package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttributeEnum;

/**
 * 设置团队空间扩展属性请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-12
 * @see
 * @since
 */
public class SetTeamSpaceAttrRequest implements Serializable
{
    private static final String UPLOAD_NOTICE_ENABLE = "enable";
    
    private static final String UPLOAD_NOTICE_DISABLE = "disable";
    
    private static final String AUTO_PRIVIEW_ENABLE = "enable";
    
    private static final String AUTO_PRIVIEW_DISABLE = "disable";
    private static final long serialVersionUID = -8245251971846052470L;
    
    private String name;
    
    private String value;
    
    public SetTeamSpaceAttrRequest()
    {
        
    }
    
    public SetTeamSpaceAttrRequest(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public void checkParameter()
    {
        if (StringUtils.isBlank(name))
        {
            throw new InvalidParamException("Name can not be null");
        }
        if (StringUtils.isBlank(value))
        {
            throw new InvalidParamException("Value can not be null");
        }
        TeamSpaceAttributeEnum attribute = TeamSpaceAttributeEnum.getTeamSpaceConfig(name);
        if (attribute == null)
        {
            throw new InvalidParamException("Invalid attribute " + name);
        }
        
        if (attribute == TeamSpaceAttributeEnum.UPLOAD_NOTICE)
        {
            if (!UPLOAD_NOTICE_ENABLE.equals(value) && !UPLOAD_NOTICE_DISABLE.equals(value))
            {
                throw new InvalidParamException("Invalid attribute value " + value);
            }
        }else if (attribute == TeamSpaceAttributeEnum.AUTO_PREVIEW)
        {
            if (!AUTO_PRIVIEW_ENABLE.equals(value) && !AUTO_PRIVIEW_DISABLE.equals(value))
            {
                throw new InvalidParamException("Invalid attribute value " + value);
            }
        }else
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

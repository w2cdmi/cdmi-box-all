package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;

/**
 * 获取团队空间扩展属性响应对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-12
 * @see
 * @since
 */
public class GetTeamSpaceAttrResponse implements Serializable
{
    
    private static final long serialVersionUID = -704780930465062776L;
    
    private List<TeamSpaceAttribute> attributes;
    
    public List<TeamSpaceAttribute> getAttributes()
    {
        return attributes;
    }
    
    public void setAttributes(List<TeamSpaceAttribute> attributes)
    {
        this.attributes = attributes;
    }
    
}

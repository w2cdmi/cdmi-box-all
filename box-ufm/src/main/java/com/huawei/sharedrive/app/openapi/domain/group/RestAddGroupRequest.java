package com.huawei.sharedrive.app.openapi.domain.group;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestAddGroupRequest
{
    private String groupRole;
    
    private RestGroupMember member;
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public RestGroupMember getMember()
    {
        return member;
    }
    
    public void setMember(RestGroupMember member)
    {
        this.member = member;
    }
    
    public void checkParameter()
    {
        if (member == null)
        {
            throw new InvalidParamException("member error");
        }
        FilesCommonUtils.checkNonNegativeIntegers(member.getUserId());
        if (groupRole == null)
        {
            groupRole = GroupConstants.ROLE_MEMBER;
        }
        if (!GroupConstants.belongAllRole(groupRole))
        {
            throw new InvalidParamException("groupRole error:" + groupRole);
        }
        if (member.getUserType() == null)
        {
            member.setUserType(GroupConstants.USERTYPE_USER);
            return;
        }
        if (!GroupConstants.belongUserType(member.getUserType()))
        {
            throw new InvalidParamException("userType error:" + member.getUserType());
        }
    }
}

package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;

import pw.cdmi.box.domain.Order;

public class ListTeamSpaceMemberRequest extends BaseListRequest
{
    
    private String teamRole;
    
    private String keyword;
    
    public ListTeamSpaceMemberRequest()
    {
        super();
    }
    
    public ListTeamSpaceMemberRequest(Integer limit, Long offset)
    {
        super(limit, offset);
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (limit != null && limit < 1)
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
        
        if (order != null)
        {
            for (Order temp : order)
            {
                temp.checkUserSpaceParameter();
            }
        }
        // 校验keyword
        if (keyword != null && keyword.length() > 255)
        {
            throw new InvalidParamException("Invalid keyword: " + keyword);
        }
        
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
}

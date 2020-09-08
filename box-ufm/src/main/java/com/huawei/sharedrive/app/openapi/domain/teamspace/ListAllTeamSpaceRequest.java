package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;

import pw.cdmi.box.domain.Order;

public class ListAllTeamSpaceRequest extends BaseListRequest
{
    private static final int MAX_LIMIT = 1000;
    
    private String keyword;
    
    private int type;
    
    private String ownerByUserName;
    
    public void checkParameter() throws InvalidParamException
    {
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
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
                temp.checkAllSpaceParameter();
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOwnerByUserName() {
		return ownerByUserName;
	}

	public void setOwnerByUserName(String ownerByUserName) {
		this.ownerByUserName = ownerByUserName;
	}

    
}

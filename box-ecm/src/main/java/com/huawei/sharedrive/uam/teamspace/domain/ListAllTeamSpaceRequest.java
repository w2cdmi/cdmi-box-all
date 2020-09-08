package com.huawei.sharedrive.uam.teamspace.domain;

public class ListAllTeamSpaceRequest extends BaseListRequest
{
    private String keyword;
    
    private int type;
    
    private String ownerByUserName;
    
    public ListAllTeamSpaceRequest()
    {
        super();
    }
    
    public ListAllTeamSpaceRequest(Integer limit, Long offset)
    {
        super(limit, offset);
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

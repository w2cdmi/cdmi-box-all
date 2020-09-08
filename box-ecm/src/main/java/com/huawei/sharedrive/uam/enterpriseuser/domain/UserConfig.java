package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;

public class UserConfig implements Serializable{

    
   
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long accountId;
    
    private String name;
    
    private String value;
    
    private long userId;
    
    public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public UserConfig()
    {
        
    }
    
    public UserConfig(long accountId,long userId, String name, String value)
    {
        this.accountId = accountId;
        this.userId = userId;
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    

}

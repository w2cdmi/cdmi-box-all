package com.huawei.sharedrive.uam.enterpriseuser.domain;


public enum UserType {
    NOMORL("nomorl"),
    OUT("out");
    private String name;
    
    private UserType(String name)
    {
        this.name = name;
    }
    public static UserType getUserType(String name)
    {
        for (UserType value : UserType.values())
        {
            if (value.getName().equals(name))
            {
                return value;
            }
        }
        return null;
    }
	private String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
    
}

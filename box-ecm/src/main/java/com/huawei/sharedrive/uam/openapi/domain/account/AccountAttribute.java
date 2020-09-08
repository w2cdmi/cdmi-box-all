package com.huawei.sharedrive.uam.openapi.domain.account;

public enum AccountAttribute
{
    SERVER_RECEIVE("serverReceive"),
    PROTOCOL_RECEIVE("protocolReceive"),
    PORT_RECEIVE("portReceive"), 
    SERVER_SEND("serverSend"), 
    PROTOCOL_SEND("protocolSend"),
    PORT_SEND("portSend");
    
    private String name;
    
    private AccountAttribute(String name)
    {
        this.name = name;
    }
    
    public static AccountAttribute getAccountAttribute(String name)
    {
        for (AccountAttribute attribute : AccountAttribute.values())
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

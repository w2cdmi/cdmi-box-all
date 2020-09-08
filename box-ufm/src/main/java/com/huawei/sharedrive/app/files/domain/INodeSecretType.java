package com.huawei.sharedrive.app.files.domain;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public enum INodeSecretType {
	

    /** 一般文档 */
    NORMOL("normol", (byte) 0),
    /** 秘密 */
    SECRET("secret", (byte) 1),
    /** 机密 */
    MIDSECRET("midsecret", (byte) 2), 
    /** 绝密 */
    TOPSECRET("topsecret", (byte)3);
    
   
    private String type;
    
    private byte value;
    
    private INodeSecretType(String type, byte value)
    {
        this.type = type;
        this.value = value;
    }
    
    public static INodeSecretType getNodeSecretType(byte value)
    {
        for (INodeSecretType nodeType : INodeSecretType.values())
        {
            if (nodeType.getValue() == value)
            {
                return nodeType;
            }
        }
        return null;
    }
    
    public static INodeSecretType getNodeSecretType(String type)
    {
        for (INodeSecretType INodeSecretType : INodeSecretType.values())
        {
            if (INodeSecretType.getType().equals(type))
            {
                return INodeSecretType;
            }
        }
        return null;
    }
    
    public static byte getValue(String type)
    {
        for (INodeSecretType INodeSecretType : INodeSecretType.values())
        {
            if (INodeSecretType.getType().equals(type))
            {
                return INodeSecretType.getValue();
            }
        }
        throw new InvalidParamException("Invalid node type " + type);
    }
    
    public static String INodeSecretType(byte value)
    {
        for (INodeSecretType INodeSecretType : INodeSecretType.values())
        {
            if (INodeSecretType.getValue() == value)
            {
                return INodeSecretType.getType();
            }
        }
        throw new InvalidParamException("Invalid node value " + value);
    }
    
    public String getType()
    {
        return type;
    }
    
    public byte getValue()
    {
        return value;
    }
    


}

package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;

/**
 * 共享对象
 * 
 * @author l90005448
 * 
 */
public class SharedUser implements Serializable
{
    private static final long serialVersionUID = -1912603051480745421L;

    private long id;
    private byte type;
    
    private Boolean isShareOwner;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }

    
}

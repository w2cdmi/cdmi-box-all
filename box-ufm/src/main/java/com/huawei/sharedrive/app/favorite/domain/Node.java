package com.huawei.sharedrive.app.favorite.domain;

import java.io.Serializable;

public class Node implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String FILE = "file";
    
    public static final String FOLDER = "folder";
    
    public static final byte FILE_NUM = 1;
    
    public static final byte FOLDER_NUM = 0;
    
    private Long ownedBy;
    
    private Long id;
    
    private Byte type;
    
    public Node()
    {
    }
    
    public Node(Long ownedBy, Long id)
    {
        this.ownedBy = ownedBy;
        this.id = id;
    }
    
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }

    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }

    
    
    public Byte getType()
    {
        return type;
    }

    public void setType(Byte type)
    {
        this.type = type;
    }

    public static String typeIntToString(byte type)
    {

        if(FILE_NUM == type)
        {
            return FILE;
        }
        if(FOLDER_NUM == type)
        {
            return FOLDER;
        }
        return "";
    }
    public static int typeStringToInt(String type)
    {

        if(null == type)
        {
            return -1;
        }
        if(FILE.equals(type))
        {
            return FILE_NUM;
        }
        if(FOLDER.equals(type))
        {
            return FOLDER_NUM;
        }
        return -1;
    }
    
}

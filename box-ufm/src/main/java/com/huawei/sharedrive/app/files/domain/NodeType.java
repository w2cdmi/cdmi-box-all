package com.huawei.sharedrive.app.files.domain;

import com.huawei.sharedrive.app.exception.InvalidParamException;

/**
 * 节点类型枚举类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum NodeType
{
    /** 文件夹 */
    FOLDER("folder", (byte) 0),
    
    /** 文件 */
    File("file", (byte) 1),
    
    /** 文件 */
    Computer("computer", (byte) -3),
    
    /** 文件 */
    Disk("disk", (byte) -2),
	  /** 文件夹 */
    WXFOLDER("folder", (byte) -5),
	
	TRANSFERFOLDER("folder", (byte) -6);
    
    private String type;
    
    private byte value;
    
    private NodeType(String type, byte value)
    {
        this.type = type;
        this.value = value;
    }
    
    public static NodeType getNodeType(byte value)
    {
        for (NodeType nodeType : NodeType.values())
        {
            if (nodeType.getValue() == value)
            {
                return nodeType;
            }
        }
        return null;
    }
    
    public static NodeType getNodeType(String type)
    {
        for (NodeType nodeType : NodeType.values())
        {
            if (nodeType.getType().equals(type))
            {
                return nodeType;
            }
        }
        return null;
    }
    
    public static byte getValue(String type)
    {
        for (NodeType nodeType : NodeType.values())
        {
            if (nodeType.getType().equals(type))
            {
                return nodeType.getValue();
            }
        }
        throw new InvalidParamException("Invalid node type " + type);
    }
    
    public static String getType(byte value)
    {
        for (NodeType nodeType : NodeType.values())
        {
            if (nodeType.getValue() == value)
            {
                return nodeType.getType();
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

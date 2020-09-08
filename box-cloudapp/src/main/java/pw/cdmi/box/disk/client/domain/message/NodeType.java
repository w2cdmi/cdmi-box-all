package pw.cdmi.box.disk.client.domain.message;

import pw.cdmi.core.exception.InvalidParamException;

/**
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum NodeType
{
    FOLDER("folder", (byte) 0),
    
    File("file", (byte) 1);
    
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
    
    public String getType()
    {
        return type;
    }
    
    public byte getValue()
    {
        return value;
    }
    
}

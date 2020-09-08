package pw.cdmi.file.engine.manage.datacenter.domain;

import pw.cdmi.core.exception.InnerException;

public enum ResourceGroupType
{
    /** 合并部署DSS */
    Merge("merge", 0),
    
    /** 分开部署DSS */
    Distribute("distribute", 1);
    
    private String type;
    
    private int value;
    
    private ResourceGroupType(String type, int value)
    {
        this.type = type;
        this.value = value;
    }
    
    public static ResourceGroupType getResourceGroupType(int value)
    {
        for (ResourceGroupType node : ResourceGroupType.values())
        {
            if (node.getValue() == value)
            {
                return node;
            }
        }
        return null;
    }
    
    public static ResourceGroupType getResourceGroupType(String type)
    {
        for (ResourceGroupType node : ResourceGroupType.values())
        {
            if (node.getType().equals(type))
            {
                return node;
            }
        }
        return null;
    }
    
    public static int getValue(String type)
    {
        for (ResourceGroupType node : ResourceGroupType.values())
        {
            if (node.getType().equals(type))
            {
                return node.getValue();
            }
        }
        throw new InnerException("Invalid ResourceGroup type " + type);
    }
    
    public static String getType(int value)
    {
        for (ResourceGroupType node : ResourceGroupType.values())
        {
            if (node.getValue() == value)
            {
                return node.getType();
            }
        }
        throw new InnerException("Invalid ResourceGroup value " + value);
    }
    
    public String getType()
    {
        return type;
    }
    
    public int getValue()
    {
        return value;
    }
    
}

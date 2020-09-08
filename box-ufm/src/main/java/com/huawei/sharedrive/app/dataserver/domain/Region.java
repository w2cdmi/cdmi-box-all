/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.domain;

import java.io.Serializable;

/**
 * @author q90003805
 * 
 */
public class Region implements Serializable
{
    /**
     * 位置存储区域的ID
     */
    public static final int UNKNOWN_REGION_ID = -2;
    
    private static final long serialVersionUID = -1356030821862518606L;
    
    private boolean defaultRegion;
    
    private String description;
    
    private int id;
    
    private String name;
    
    public String getDescription()
    {
        return description;
    }
    
    public int getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean isDefaultRegion()
    {
        return defaultRegion;
    }
    
    public void setDefaultRegion(boolean defaultRegion)
    {
        this.defaultRegion = defaultRegion;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}

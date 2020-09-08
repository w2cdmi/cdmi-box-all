/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author q90003805
 * 
 */
public class Region implements Serializable
{
    private static final long serialVersionUID = -1356030821862518606L;
    
    private int id;
    
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    
    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9]*$")
    private String code;
    
    @Size(max = 255)
    private String description;
    
    private boolean defaultRegion;
    
    private List<DataCenter> dataCenters = new ArrayList<DataCenter>(10);
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public boolean isDefaultRegion()
    {
        return defaultRegion;
    }
    
    public void setDefaultRegion(boolean defaultRegion)
    {
        this.defaultRegion = defaultRegion;
    }
    
    public List<DataCenter> getDataCenters()
    {
        return dataCenters;
    }
    
    public void setDataCenters(List<DataCenter> dataCenters)
    {
        this.dataCenters = dataCenters;
    }
}

package pw.cdmi.box.disk.favorite.domain;

import java.io.Serializable;

public class Param implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2041020053337950761L;
    
    private String name;
    
    private String value;
    
    public Param()
    {
    }
    
    public Param(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public enum Name
    {
        ORGINNAME("orginName"), TEAMSPACENAME("teamspaceName"), SENDER("sender"), LINKCODE("linkCode"), PATH(
            "path");
        
        private Name(String name)
        {
            this.name = name;
        }
        
        private String name;
        
        public String getName()
        {
            return name;
        }
        
    }
}

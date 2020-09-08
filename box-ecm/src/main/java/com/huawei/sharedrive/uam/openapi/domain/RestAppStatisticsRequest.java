package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestAppStatisticsRequest implements Serializable
{
    public static enum StatisticsType
    {
        teamSpace("teamspace"),
        
        total("total"),
        
        user("user");
        
        private String type;
        
        private StatisticsType(String type)
        {
            this.type = type;
        }
        
        public String getType()
        {
            return type;
        }
    }
    
    private static final long serialVersionUID = -7797664500622841235L;
    
    private String type;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
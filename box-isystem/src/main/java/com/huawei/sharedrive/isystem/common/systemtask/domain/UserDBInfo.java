package com.huawei.sharedrive.isystem.common.systemtask.domain;

public class UserDBInfo
{
    private String dbName;
    
    private String dbBeanName;
    
    private int dbNumber;
    
    public String getDbBeanName()
    {
        return dbBeanName;
    }
    
    public String getDbName()
    {
        return dbName;
    }
    
    public int getDbNumber()
    {
        return dbNumber;
    }
    
    public void setDbBeanName(String dbBeanName)
    {
        this.dbBeanName = dbBeanName;
    }
    
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
    
    public void setDbNumber(int dbNumber)
    {
        this.dbNumber = dbNumber;
    }
}

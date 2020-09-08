package com.huawei.sharedrive.app.common.systemtask.domain;

public class UserDBInfo
{
    private String dbName;
    private String dbBeanName;
    private int dbNumber;
    public String getDbName()
    {
        return dbName;
    }
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
    public String getDbBeanName()
    {
        return dbBeanName;
    }
    public void setDbBeanName(String dbBeanName)
    {
        this.dbBeanName = dbBeanName;
    }
    public int getDbNumber()
    {
        return dbNumber;
    }
    public void setDbNumber(int dbNumber)
    {
        this.dbNumber = dbNumber;
    }
}

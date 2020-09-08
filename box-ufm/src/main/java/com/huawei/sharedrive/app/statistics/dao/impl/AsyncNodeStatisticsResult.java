package com.huawei.sharedrive.app.statistics.dao.impl;

public class AsyncNodeStatisticsResult
{
    
    private String dbName;
    
    private Exception exception;

    public String getDbName()
    {
        return dbName;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }
    
    
    
}

package com.huawei.sharedrive.isystem.common.systemtask.domain;

import java.util.Date;

import pw.cdmi.core.utils.JsonUtils;

public class ScanTableInfo
{
    
    private String dbName;
    
    private int dbNumber;
    
    private Date lastModfied;
    
    private String tableName;
    
    private int tableNumber;
    
    public static String toJsonStr(ScanTableInfo tableInfo)
    {
        return JsonUtils.toJson(tableInfo);
    }
    
    public static ScanTableInfo toObject(String jsonStr)
    {
        return JsonUtils.stringToObject(jsonStr, ScanTableInfo.class);
    }
    
    public String getDbName()
    {
        return dbName;
    }
    
    public int getDbNumber()
    {
        return dbNumber;
    }
    
    public Date getLastModfied()
    {
        if (this.lastModfied != null)
        {
            return new Date(this.lastModfied.getTime());
        }
        return null;
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public int getTableNumber()
    {
        return tableNumber;
    }
    
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
    
    public void setDbNumber(int dbNumber)
    {
        this.dbNumber = dbNumber;
    }
    
    public void setLastModfied(Date lastModfied)
    {
        this.lastModfied = lastModfied != null ? new Date(lastModfied.getTime()) : null;
    }
    
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }
    
    public void setTableNumber(int tableNumber)
    {
        this.tableNumber = tableNumber;
    }
    
    public String toStr()
    {
        String info = "dbName:" + this.dbName + ",tableName" + this.tableName;
        if (null != this.lastModfied)
        {
            info = info + this.lastModfied.toString();
        }
        
        return info;
        
    }
    
}

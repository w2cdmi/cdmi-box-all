package com.huawei.sharedrive.app.security.domain;

public enum CheckEngine
{
    /** 通用的安全矩阵校验 */
    STANDARD("standard"),
    
    /** 华为云盘安全矩阵校验 */
    HUAWEI("huawei");
    
    private String name;
    
    private CheckEngine(String name)
    {
        this.name = name;
    }
    
    public static CheckEngine getCheckEngine(String name)
    {
        for (CheckEngine checkEngine : CheckEngine.values())
        {
            if (checkEngine.getName().equals(name))
            {
                return checkEngine;
            }
        }
        return STANDARD;
    }
    
    public String getName()
    {
        return name;
    }
    
}

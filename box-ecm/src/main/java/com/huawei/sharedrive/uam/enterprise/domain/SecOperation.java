package com.huawei.sharedrive.uam.enterprise.domain;

import java.util.ArrayList;
import java.util.List;

public enum SecOperation
{
    UPLOAD("upload", 2), DOWNLOAD("download", 3), CREATE_FOLDER("create", 5), PREVIEW("preview", 13), SHARE(
        "share", 17), LINK("link", 19), BROWSER("browser", 23), DELETE("delete", 29);
    
    private String name;
    
    private long intValue;
    
    private SecOperation(String name, int intValue)
    {
        this.name = name;
        this.intValue = intValue;
    }
    
    public long getIntValue()
    {
        return intValue;
    }
    
    public static SecOperation getSecOperation(String name)
    {
        for (SecOperation tempOper : SecOperation.values())
        {
            if (tempOper.name.equals(name))
            {
                return tempOper;
            }
        }
        return null;
    }
    
    public static SecOperation getSecOperation(long intValue)
    {
        for (SecOperation tempOper : SecOperation.values())
        {
            if (tempOper.intValue == intValue)
            {
                return tempOper;
            }
        }
        return null;
    }
    
    public static List<SecOperation> getSecOperations(long mulValue)
    {
        List<SecOperation> list = new ArrayList<SecOperation>(10);
        for (SecOperation tempOper : SecOperation.values())
        {
            if (mulValue % tempOper.intValue == 0)
            {
                list.add(tempOper);
            }
        }
        return list;
    }
}

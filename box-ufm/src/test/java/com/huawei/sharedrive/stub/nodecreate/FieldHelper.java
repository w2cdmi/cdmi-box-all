package com.huawei.sharedrive.stub.nodecreate;

import java.util.UUID;

public class FieldHelper
{
    
    public static int getResourceGroup()
    {
        return 2;
    }
    
    public static String getRandomFolderName()
    {
        return UUID.randomUUID().toString();
    }
    
    public static String getRandomFileName()
    {
        return UUID.randomUUID().toString() + ".dat";
    }
    
}

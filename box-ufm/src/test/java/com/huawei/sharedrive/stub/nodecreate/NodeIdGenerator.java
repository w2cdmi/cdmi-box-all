package com.huawei.sharedrive.stub.nodecreate;

public class NodeIdGenerator
{
    
    private static long backNodeId = 1000000;
    
    private static long normalNodeId = 1000000;
    
    public static long getNextBackNodeId()
    {
        return backNodeId ++* 2;
    }
    
    public static long getNextNormailNodeId()
    {
        return normalNodeId ++* 2 - 1;
    }
    
    
}

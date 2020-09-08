package com.huawei.sharedrive.app.dataserver.thrift.client;

public final class ThriftBaseParamter
{
    private ThriftBaseParamter()
    {
        
    }
    public final static String DATA_CENTER_THRIFT_SERVICE = "DataCenterThriftService";
    
    public final static String FILE_OBJECT_THRIFT_SERVICE = "FileObjectThriftService";
    
    public final static String STORAGE_RESOUCE_THRIFT_SERVICE = "StorageResouceThriftService";
    
    private static int dataServerPort;
    
    private static int timeout;
    
    public static int getDataServerPort()
    {
        return ThriftBaseParamter.dataServerPort;
    }
    
    public static int getTimeout()
    {
        return ThriftBaseParamter.timeout;
    }
    
    public static void setDataServerPort(int dataServerPort)
    {
        ThriftBaseParamter.dataServerPort = dataServerPort;
    }
    
    public static void setTimeout(int timeout)
    {
        ThriftBaseParamter.timeout = timeout;
    }
}

package com.huawei.sharedrive.app.openapi.domain.statistics;

public final class SizeUtils
{
    private SizeUtils()
    {
        
    }
    public static long getMbSize(Long size)
    {
        if(null == size)
        {
            return 0;
        }
        if(size % (1024 * 1024) == 0)
        {
            return size / (1024 * 1024);
        }
        return size / (1024 * 1024) + 1;
    }
}

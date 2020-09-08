package com.huawei.sharedrive.app.files.dao.batchparam;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public final class BatchParams
{
    
    private BatchParams()
    {
        
    }
    
    public static int getBatchUpdateItems()
    {
        try
        {
            return Integer.parseInt(PropertiesUtils.getProperty("batch.update", "2000"));
        }
        catch(NumberFormatException e)
        {
            return 2000;
        }
    }
    
    public static int getBatchQueryItems()
    {
        try
        {
            return Integer.parseInt(PropertiesUtils.getProperty("batch.query", "2000"));
        }
        catch(NumberFormatException e)
        {
            return 2000;
        }
    }
    
}

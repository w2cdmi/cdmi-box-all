package com.huawei.sharedrive.app.openapi.checker;

import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.exception.NoSuchReginException;

public final class RegionChecker
{
    private RegionChecker()
    {
        
    }
    
    public static void checkAppIdAllowEmpty(Integer regionId, RegionService regionService)
    {
        if(null == regionId)
        {
            return;
        }
        Region region = regionService.getRegion(regionId);
        if(null == region)
        {
            throw new NoSuchReginException();
        }
    }
}

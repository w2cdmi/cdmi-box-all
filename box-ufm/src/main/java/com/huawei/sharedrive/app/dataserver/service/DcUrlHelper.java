package com.huawei.sharedrive.app.dataserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.tool.RegionSpareRelation;
import com.huawei.sharedrive.app.dataserver.service.tool.RegionSpareUtils;


@Service("dcUrlHelper")
public class DcUrlHelper
{
    @Autowired
    private DCManager dcManager;
    
    public ResourceGroup getAavailableResourceGroup(int region)
    {
        ResourceGroup resourceGroup = dcManager.selectBestGroupRwNormal(region);
        if(null != resourceGroup)
        {
            return resourceGroup;
        }
        RegionSpareRelation tempSpareRelation = null;
        int spareSize = RegionSpareUtils.getSpareSize(region);
        for(int priority = 1; priority <= spareSize; priority++)
        {
            tempSpareRelation = RegionSpareUtils.getRegionSpare(region, priority);
            if(null == tempSpareRelation)
            {
                continue;
            }
            resourceGroup = dcManager.selectBestGroupRwNormal(tempSpareRelation.getSpareRegion());
            if(null != resourceGroup)
            {
                return resourceGroup;
            }
        }
        return resourceGroup;
    }
    
    
}

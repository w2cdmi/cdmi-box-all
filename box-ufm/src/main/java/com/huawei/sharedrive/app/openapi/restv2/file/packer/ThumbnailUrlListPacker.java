package com.huawei.sharedrive.app.openapi.restv2.file.packer;

import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;

public final class ThumbnailUrlListPacker
{
    private ThumbnailUrlListPacker()
    {
        
    }
    
    public static void transThumbnailUrlList(RestFileInfo restFileInfo)
    {
        if (restFileInfo == null || restFileInfo.getThumbnailUrlList() == null)
        {
            return;
        }
        if (restFileInfo.getThumbnailUrlList().isEmpty())
        {
            restFileInfo.setThumbnailUrlList(null);
        }
    }
    
}

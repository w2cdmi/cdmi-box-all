package com.huawei.sharedrive.app.openapi.restv2.share;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.InvalidParamException;

public final class ThumbnailRequestChecker
{
    private ThumbnailRequestChecker()
    {
        
    }
    
    private final static int MAX_THUMBLIST = 5;
    
    public static void checkThumbnail(List<Thumbnail> thumbList) throws InvalidParamException
    {
        if(CollectionUtils.isEmpty(thumbList))
        {
            return;
        }
        if(thumbList.size() > MAX_THUMBLIST)
        {
            throw new InvalidParamException("Invalid thumbList size " + thumbList.size());
        }
        for(Thumbnail thumb: thumbList)
        {
            if(thumb.getWidth() <= 0 || thumb.getHeight() <= 0)
            {
                throw new InvalidParamException("Invalid thumb: " + thumb.getWidth() + ',' + thumb.getHeight());
            }
        }
    }
    
}

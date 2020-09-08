package com.huawei.sharedrive.app.openapi.domain.link;

import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfoV2;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;

/**
 * 外链和节点对象,适用于通过提取码获取外链对象和节点对象
 * 
 * @author l90005448
 * 
 */

public class LinkAndNodeV2
{
    
    private INodeLink link;
    
    private RestFileInfoV2 file;
    
    private RestFolderInfo folder;
    
    public INodeLink getLink()
    {
        return link;
    }
    
    public void setLink(INodeLink link)
    {
        this.link = link;
    }
    
    public RestFileInfoV2 getFile()
    {
        return file;
    }
    
    public void setFile(RestFileInfoV2 file)
    {
        this.file = file;
    }
    
    public RestFolderInfo getFolder()
    {
        return folder;
    }
    
    public void setFolder(RestFolderInfo folder)
    {
        this.folder = folder;
    }
    
}

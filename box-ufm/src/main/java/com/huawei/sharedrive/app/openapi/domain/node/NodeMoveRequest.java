package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class NodeMoveRequest
{
    // 与目标文件夹下的文件名/文件夹名冲突时, 是否自动重命名
    private Boolean autoRename;
    
    // 目标文件夹拥有者ID
    private Long destOwnerId;
    
    // 目标文件夹ID
    private Long destParent;
    
    public NodeMoveRequest()
    {
        
    }
    
    public NodeMoveRequest(Long destParent, Long destOwnerId, Boolean autoRename)
    {
        this.destParent = destParent;
        this.destOwnerId = destOwnerId;
        this.autoRename = autoRename;
    }
    
    // TODO
    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNonNegativeIntegers(destOwnerId, destParent);
        if (autoRename == null)
        {
            throw new InvalidParamException();
        }
    }
    
    public Long getDestOwnerId()
    {
        return destOwnerId;
    }
    
    public Long getDestParent()
    {
        return destParent;
    }
    
    public Boolean isAutoRename()
    {
        return autoRename;
    }
    
    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
    }
    
    public void setDestOwnerId(Long destOwnerId)
    {
        this.destOwnerId = destOwnerId;
    }
    
    public void setDestParent(Long destParent)
    {
        this.destParent = destParent;
    }
}

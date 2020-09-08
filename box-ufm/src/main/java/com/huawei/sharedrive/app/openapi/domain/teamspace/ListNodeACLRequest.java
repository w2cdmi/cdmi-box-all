package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class ListNodeACLRequest extends BaseListRequest
{
    
    private Long nodeId;
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public ListNodeACLRequest()
    {
        super();
    }
    
    public ListNodeACLRequest(Integer limit, Long offset)
    {
        super(limit, offset);
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (nodeId != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(nodeId);
        }
        
        if (limit != null && limit < 1)
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
        
    }
    
}

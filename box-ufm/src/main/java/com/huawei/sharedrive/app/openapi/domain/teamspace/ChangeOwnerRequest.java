package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class ChangeOwnerRequest
{
    private Long newOwnerId;

    public Long getNewOwnerId()
    {
        return newOwnerId;
    }

    public void setNewOwnerId(Long newOwnerId)
    {
        this.newOwnerId = newOwnerId;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNonNegativeIntegers(newOwnerId);
    }
    
}

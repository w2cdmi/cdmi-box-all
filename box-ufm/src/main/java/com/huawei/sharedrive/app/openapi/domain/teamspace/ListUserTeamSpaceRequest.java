package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Order;

public class ListUserTeamSpaceRequest extends BaseListRequest
{
    private static final int MAX_LIMIT = 1000;
    private Long userId;
    private int type;

    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNonNegativeIntegers(userId);
        
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
        
        if (order != null)
        {
            for (Order temp : order)
            {
                temp.checkUserSpaceParameter();
            }
        }
        
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

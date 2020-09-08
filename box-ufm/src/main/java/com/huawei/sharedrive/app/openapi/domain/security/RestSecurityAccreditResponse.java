package com.huawei.sharedrive.app.openapi.domain.security;

public class RestSecurityAccreditResponse extends RestSceurityAccreditRequest
{
    private boolean canAccess;
    
    public RestSecurityAccreditResponse()
    {
        super();
    }
    
    public RestSecurityAccreditResponse(RestSceurityAccreditRequest request)
    {
        super();
        this.setiNodeId(request.getiNodeId());
        this.setOnwerCloudUserId(request.getOnwerCloudUserId());
        this.setPermissions(request.getPermissions());
        this.setType(request.getType());
    }
    
    public boolean isCanAccess()
    {
        return canAccess;
    }
    
    public void setCanAccess(boolean canAccess)
    {
        this.canAccess = canAccess;
    }
}

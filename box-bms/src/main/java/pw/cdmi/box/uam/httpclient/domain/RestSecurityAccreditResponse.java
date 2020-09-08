package pw.cdmi.box.uam.httpclient.domain;

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
        this.setClientAddress(request.getClientAddress());
        this.setiNodeId(request.getiNodeId());
        this.setOnwerCloudUserId(request.getOnwerCloudUserId());
        this.setPermissions(request.getPermissions());
        this.setProxyAddress(request.getProxyAddress());
        this.setType(request.getType());
        this.setVisitCloudUserId(request.getVisitCloudUserId());
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

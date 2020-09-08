package pw.cdmi.box.disk.httpclient.rest.request;

public class RestSceurityAccreditRequest
{
    
    private String clientAddress;
    
    private String proxyAddress;
    
    private long visitCloudUserId;
    
    private long iNodeId;
    
    private byte type;
    
    private long onwerCloudUserId;
    
    private int deviceType;
    
    private String mimeType;
    
    private String[] permissions;
    
    public String getClientAddress()
    {
        return clientAddress;
    }
    
    public void setClientAddress(String clientAddress)
    {
        this.clientAddress = clientAddress;
    }
    
    public String getProxyAddress()
    {
        return proxyAddress;
    }
    
    public void setProxyAddress(String proxyAddress)
    {
        this.proxyAddress = proxyAddress;
    }
    
    public long getVisitCloudUserId()
    {
        return visitCloudUserId;
    }
    
    public void setVisitCloudUserId(long visitCloudUserId)
    {
        this.visitCloudUserId = visitCloudUserId;
    }
    
    public long getiNodeId()
    {
        return iNodeId;
    }
    
    public void setiNodeId(long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public long getOnwerCloudUserId()
    {
        return onwerCloudUserId;
    }
    
    public void setOnwerCloudUserId(long onwerCloudUserId)
    {
        this.onwerCloudUserId = onwerCloudUserId;
    }
    
    public String[] getPermissions()
    {
        return permissions != null ? permissions.clone() : new String[0];
    }
    
    public void setPermissions(String[] permissions)
    {
        if (permissions != null)
        {
            this.permissions = permissions.clone();
        }
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getMimeType()
    {
        return mimeType;
    }
    
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
}

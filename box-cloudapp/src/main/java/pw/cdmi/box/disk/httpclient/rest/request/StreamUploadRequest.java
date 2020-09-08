package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.InputStream;
import java.io.Serializable;

public class StreamUploadRequest implements Serializable
{
    private static final long serialVersionUID = 1845755964079517636L;
    
    private String parentId;
    
    private String ownerID;
    
    private String authorization;
    
    private String sha1;
    
    private transient InputStream resouce;
    
    private String fileName;
    
    public String getParentId()
    {
        return parentId;
    }
    
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }
    
    public String getOwnerID()
    {
        return ownerID;
    }
    
    public void setOwnerID(String ownerID)
    {
        this.ownerID = ownerID;
    }
    
    public String getAuthorization()
    {
        return authorization;
    }
    
    public void setAuthorization(String authorization)
    {
        this.authorization = authorization;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public InputStream getResouce()
    {
        return resouce;
    }
    
    public void setResouce(InputStream resouce)
    {
        this.resouce = resouce;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}

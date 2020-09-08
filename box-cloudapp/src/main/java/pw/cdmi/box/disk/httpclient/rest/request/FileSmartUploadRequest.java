package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class FileSmartUploadRequest implements Serializable
{
    
    private static final long serialVersionUID = -1010293498731043470L;
    
    private String ownerID;
    
    private String filePath;
    
    private String uptoFolderID;
    
    private String authorization;
    
    private String sha1;
    
    public String getSha1()
    {
        return sha1;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public String getOwnerID()
    {
        return ownerID;
    }
    
    public void setOwnerID(String ownerID)
    {
        this.ownerID = ownerID;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    public String getUptoFolderID()
    {
        return uptoFolderID;
    }
    
    public void setUptoFolderID(String uptoFolderID)
    {
        this.uptoFolderID = uptoFolderID;
    }
    
    public String getAuthorization()
    {
        return authorization;
    }
    
    public void setAuthorization(String authorization)
    {
        this.authorization = authorization;
    }
    
}

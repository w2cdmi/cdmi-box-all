package pw.cdmi.box.disk.httpclient.rest.request;

public class FileuploadParts
{
    
    private String partID;
    
    public FileuploadParts()
    {
    }
    
    public FileuploadParts(String partID)
    {
        this.partID = partID;
    }
    
    public String getPartID()
    {
        return partID;
    }
    
    public void setPartID(String partID)
    {
        this.partID = partID;
    }
    
    @Override
    public String toString()
    {
        return "FileuploadParts [partID=" + partID + ", getPartID()=" + getPartID() + ", getClass()="
            + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + ']';
    }
    
}

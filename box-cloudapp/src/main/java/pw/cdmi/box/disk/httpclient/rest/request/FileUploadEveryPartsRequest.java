package pw.cdmi.box.disk.httpclient.rest.request;

import java.util.List;

public class FileUploadEveryPartsRequest
{
    
    private List<FileuploadParts> parts;
    
    public List<FileuploadParts> getParts()
    {
        return parts;
    }
    
    public void setParts(List<FileuploadParts> parts)
    {
        this.parts = parts;
    }
    
    @Override
    public String toString()
    {
        return "File_SinglePartInfo_UploadByMutiMart_Response [parts=" + parts + ']';
    }
    
}

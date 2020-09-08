package pw.cdmi.box.disk.httpclient.rest.response;

import java.io.Serializable;
import java.util.List;

public class FileAllPartInfoResponse extends BaseResponse implements Serializable
{
    
    private static final long serialVersionUID = -5783360461650281170L;
    
    private List<FileSinglePartInfo> parts;
    
    public List<FileSinglePartInfo> getParts()
    {
        return parts;
    }
    
    public void setParts(List<FileSinglePartInfo> parts)
    {
        this.parts = parts;
    }
    
    @Override
    public String toString()
    {
        return "File_SinglePartInfo_UploadByMutiMart_Response [parts=" + parts + ']';
    }
    
}

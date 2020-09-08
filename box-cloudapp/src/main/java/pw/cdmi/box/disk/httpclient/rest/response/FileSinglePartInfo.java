package pw.cdmi.box.disk.httpclient.rest.response;

import java.io.Serializable;

public class FileSinglePartInfo extends BaseResponse implements Serializable
{
    
    private static final long serialVersionUID = -1802535827628081783L;
    
    public FileSinglePartInfo()
    {
    }
    
    public FileSinglePartInfo(int partID, long size)
    {
        this.partID = partID;
        this.size = size;
    }
    
    private int partID;
    
    private long size;
    
    public int getPartID()
    {
        return partID;
    }
    
    public void setPartID(int partID)
    {
        this.partID = partID;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    @Override
    public String toString()
    {
        return "File_SinglePartInfo [part_id=" + partID + ", size=" + size + ']';
    }
    
}

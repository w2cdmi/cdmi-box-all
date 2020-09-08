package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

public class SharePageV2 implements Serializable
{
    private static final long serialVersionUID = 3982404042036732958L;
    
    private List<INodeShareV2> contents;
    
    private int limit;
    
    private int offset;
    
    private int totalCount;
    
    public List<INodeShareV2> getContents()
    {
        return contents;
    }
    
    public void setContents(List<INodeShareV2> contents)
    {
        this.contents = contents;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public int getOffset()
    {
        return offset;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}

package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

public class MySharesPage implements Serializable
{
    private static final long serialVersionUID = 3982404042036732958L;
    
    private List<INodeShareV2> contents;
    
    private Integer limit;
    
    private Long offset;
    
    private Long totalCount;
    
    public MySharesPage()
    {
    }
    
    public MySharesPage(List<INodeShareV2> contents, Long totalCount)
    {
        this.totalCount = totalCount;
        this.contents = contents;
    }
    
    public List<INodeShareV2> getContents()
    {
        return contents;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public void setContents(List<INodeShareV2> contents)
    {
        this.contents = contents;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}

package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

public class SharePage implements Serializable
{
    private static final long serialVersionUID = -3763564386702721321L;
    
    private List<INodeShare> content;
    
    private int totalCount;
    
    public SharePage(List<INodeShare> content, int totalCount)
    {
        this.totalCount = totalCount;
        this.content = content;
    }
    
    public List<INodeShare> getContent()
    {
        return content;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setContent(List<INodeShare> content)
    {
        this.content = content;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}

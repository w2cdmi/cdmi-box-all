package pw.cdmi.box.disk.client.domain.node;

import java.util.List;

import pw.cdmi.box.disk.client.domain.common.PageResult;
import pw.cdmi.box.disk.favorite.domain.FavoriteNode;

public class RestFavoriteLists extends PageResult
{
    public RestFavoriteLists()
    {
        
    }
    
    public RestFavoriteLists(int limit, int offset, int totalCount)
    {
        super(limit, offset, totalCount);
    }
    private List<FavoriteNode> contents;
    public List<FavoriteNode> getContents()
    {
        return contents;
    }

    public void setContents(List<FavoriteNode> contents)
    {
        this.contents = contents;
    }
    
}

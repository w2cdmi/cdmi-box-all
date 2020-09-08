package pw.cdmi.box.uam.httpclient.domain;

public class ListAllTeamSpaceRequest extends BaseListRequest
{
    private String keyword;
    
    public ListAllTeamSpaceRequest()
    {
        super();
    }
    
    public ListAllTeamSpaceRequest(Integer limit, Long offset)
    {
        super(limit, offset);
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
}

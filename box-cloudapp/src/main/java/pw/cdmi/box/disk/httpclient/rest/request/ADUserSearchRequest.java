package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class ADUserSearchRequest implements Serializable
{
    private static final long serialVersionUID = -4310413222632823628L;
    
    private String keyword;
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}

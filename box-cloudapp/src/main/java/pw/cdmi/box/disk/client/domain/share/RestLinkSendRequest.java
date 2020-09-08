package pw.cdmi.box.disk.client.domain.share;

public class RestLinkSendRequest
{
    private String emails;
    
    private String linkUrl;
    
    private String message;
    
    private Integer totalCount;
    
    public String getEmails()
    {
        return emails;
    }
    
    public String getLinkUrl()
    {
        return linkUrl;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public void setEmails(String emails)
    {
        this.emails = emails;
    }
    
    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }
}

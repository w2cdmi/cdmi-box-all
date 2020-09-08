package pw.cdmi.box.disk.share.domain;


public class LinkRequest extends BasicLink
{
    private String accessCode;
    
    private String effectiveAt;

    private String expireAt;

    private String timeZone;
    
    public String getAccessCode()
    {
        return accessCode;
    }

    public String getEffectiveAt()
    {
        return effectiveAt;
    }

    public String getExpireAt()
    {
        return expireAt;
    }

    public String getTimeZone()
    {
        return timeZone;
    }
    
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }
    
    public void setEffectiveAt(String effectiveAt)
    {
        this.effectiveAt = effectiveAt;
    }

    public void setExpireAt(String expireAt)
    {
        this.expireAt = expireAt;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }
    
}

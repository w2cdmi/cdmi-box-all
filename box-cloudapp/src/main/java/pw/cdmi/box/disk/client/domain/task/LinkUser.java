package pw.cdmi.box.disk.client.domain.task;

public class LinkUser
{
    private String linkCode;
    
    private String plainAccessCode;

    public String getLinkCode()
    {
        return linkCode;
    }

    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }

    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }

    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
}

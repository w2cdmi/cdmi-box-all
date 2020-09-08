package pw.cdmi.box.disk.client.domain.share;

public class RestLinkDynamicResponse
{
    private String accessCodeMode;
    
    private String plainAccessCode;

    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }

    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }

    public String getAccessCodeMode()
    {
        return accessCodeMode;
    }

    public void setAccessCodeMode(String accessCodeMode)
    {
        this.accessCodeMode = accessCodeMode;
    }

    
}

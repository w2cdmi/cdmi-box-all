package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PreviewUrlResponse
{
    private String url;
    private String resultCode = "00000000";

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}

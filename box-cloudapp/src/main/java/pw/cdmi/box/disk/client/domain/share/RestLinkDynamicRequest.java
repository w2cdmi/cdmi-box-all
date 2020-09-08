package pw.cdmi.box.disk.client.domain.share;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InvalidParamException;


public class RestLinkDynamicRequest
{
    private String identity;

    private String linkCode;

    public void checkParameter() throws InvalidParamException
    {
        if(StringUtils.isBlank(linkCode))
        {
            throw new InvalidParamException("linkCode is blank");
        }
        
        if (StringUtils.isBlank(identity))
        {
            throw new InvalidParamException("identity is blank");
        }
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public String getIdentity()
    {
        return identity;
    }
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }
}

package pw.cdmi.box.disk.share.domain.mail;

import java.io.Serializable;
import java.util.List;

public class RestMailSendRequest implements Serializable
{
    
    private static final long serialVersionUID = 7430647539391996733L;
    
    private String type;
    
    private List<RequestMail> mailTo;
    
    private List<RequestMail> copyTo;
    
    private List<RequestAttribute> params;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public List<RequestMail> getMailTo()
    {
        return mailTo;
    }
    
    public void setMailTo(List<RequestMail> mailTo)
    {
        this.mailTo = mailTo;
    }
    
    public List<RequestMail> getCopyTo()
    {
        return copyTo;
    }
    
    public void setCopyTo(List<RequestMail> copyTo)
    {
        this.copyTo = copyTo;
    }
    
    public List<RequestAttribute> getParams()
    {
        return params;
    }
    
    public void setParams(List<RequestAttribute> params)
    {
        this.params = params;
    }
    
}

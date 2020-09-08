package pw.cdmi.box.uam.system.domain;

import org.apache.commons.mail.HtmlEmail;

public class HtmlEmailVo extends HtmlEmail
{
    private Long mailServerId;
    
    public Long getMailServerId()
    {
        return mailServerId;
    }
    
    public void setMailServerId(Long mailServerId)
    {
        this.mailServerId = mailServerId;
    }
    
}

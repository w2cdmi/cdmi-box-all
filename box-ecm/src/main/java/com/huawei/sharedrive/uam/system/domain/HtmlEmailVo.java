package com.huawei.sharedrive.uam.system.domain;

import org.apache.commons.mail.HtmlEmail;

public class HtmlEmailVo extends HtmlEmail
{
    private Long mailServerId;
    
    private String sendToUsername;
    
    public String getSendToUsername()
    {
        return sendToUsername;
    }
    
    public void setSendToUsername(String sendToUsername)
    {
        this.sendToUsername = sendToUsername;
    }
    
    public Long getMailServerId()
    {
        return mailServerId;
    }
    
    public void setMailServerId(Long mailServerId)
    {
        this.mailServerId = mailServerId;
    }
    
}

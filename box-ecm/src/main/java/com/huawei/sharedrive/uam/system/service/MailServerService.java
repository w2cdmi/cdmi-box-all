package com.huawei.sharedrive.uam.system.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.EmailException;

import com.huawei.sharedrive.uam.system.domain.MailServer;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface MailServerService
{
    
    MailServer getMailServer(Long id);
    
    MailServer getMailServerByAppId(String appId);
    
    List<MailServer> getMailServerList(MailServer filter, Order order, Limit limit);
    
    void deleteMailServer(Long id);
    
    MailServer getDefaultMailServer();
    
    boolean checkAllMailServer();
    
    /**
     * 
     * @param id
     * @param reciver
     * @throws EmailException
     * @throws IOException
     */
    void sendTestMail(Long id, String reciver) throws EmailException, IOException;
    
    void sendTestMail(MailServer mailServer, String reciver) throws EmailException, IOException;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendHtmlMail(String toUsername, Long mailServerId, String to, String cc, String bcc, String subject,
        String msg);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendHtmlMail(String shareName, Long mailServerId, String[] to, String[] cc, String subject,
        String msg);
    
    /**
     * 
     * @param templateStr
     * @param messageModel
     * @return
     * @throws IOException
     */
    String getEmailMsgByTemplate(String templateStr, Map<String, Object> messageModel) throws IOException;

	MailServer getByAccountId(long accountId);
}

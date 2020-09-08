/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.mail.EmailException;

import com.huawei.sharedrive.isystem.exception.BusinessException;

import pw.cdmi.common.domain.MailServer;

/**
 * @author s00108907
 * 
 */
public interface MailServerService
{
    
    /**
     * 获取当前的邮件服务器配置
     * 
     * @return
     */
    MailServer getMailServer();
    
    /**
     * 保存邮件配置信息
     * 
     * @param mailServer
     */
    void saveMailServer(MailServer mailServer);
    
    /**
     * 定时任务的方法，拥有检查邮箱服务器是否正常
     */
    void checkMailServer() throws BusinessException;
    
    /**
     * 向收件人发送测试邮件
     * 
     * @param id
     * @param reciver
     * @throws EmailException
     * @throws IOException
     */
    void sendTestMail(MailServer mailServer, String reciver) throws EmailException, IOException;
    
    /**
     * 发送普通文本邮件
     * 
     * @param to 收件人
     * @param cc 抄送人
     * @param bcc 密送人
     * @param subject 主题
     * @param msg 邮件内容
     */
    void sendTextMail(String to, String cc, String bcc, String subject, String msg);
    
    /**
     * 发送HTML格式邮件
     * 
     * @param to 收件人
     * @param cc 抄送人
     * @param bcc 密送人
     * @param subject 主题
     * @param msg 邮件内容
     */
    void sendHtmlMail(String to, String cc, String bcc, String subject, String msg);
    
    /**
     * 通过模板获取邮件信息
     * 
     * @param templateStr
     * @param messageModel
     * @return
     * @throws IOException
     */
    String getEmailMsgByTemplate(String templateStr, Map<String, Object> messageModel) throws IOException;
}

/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.system.service.MailServerService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.util.FreeMarkers;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.domain.MailServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.alarm.MailFailedAlarm;
import pw.cdmi.core.utils.EDToolsEnhance;

/**
 * @author s00108907
 * 
 */
@Component
public class MailServerServiceImpl extends QuartzJobTask implements MailServerService
{
    /**
     * 异步邮件发送线程任务
     * 
     * @author s00108907
     * 
     */
    private class SendMailTask implements Runnable
    {
        public void run()
        {
            while (true)
            {
                if (trySendMail())
                {
                    break;
                }
            }
        }
        
        private boolean trySendMail()
        {
            boolean interrupted = false;
            try
            {
                Email email = mailQueue.take();
                email.setCharset("utf-8");
                MailServer mailServer = getMailServer();
                if (mailServer == null)
                {
                    LOGGER.error("MailServer won't configed!");
                    return interrupted;
                }
                email.setHostName(mailServer.getServer());
                String ipSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.getDefault());
                if ("ssl".equals(ipSecurity) || "tls".equals(ipSecurity))
                {
                    email.setSSLOnConnect(true);
                    email.setSslSmtpPort(mailServer.getPort() + "");
                }
                else if ("false".equals(ipSecurity))
                {
                    email.setSmtpPort(mailServer.getPort());
                }
                else
                {
                    LOGGER.error("the type of mailIPSecurity is error:", ipSecurity);
                    return false;
                }
                if (mailServer.isEnableAuth())
                {
                    email.setAuthenticator(new DefaultAuthenticator(mailServer.getAuthUsername(),
                        mailServer.getAuthPassword()));
                }
                email.setFrom(mailServer.getSenderMail(), mailServer.getSenderName());
                email.send();
            }
            catch (InterruptedException e)
            {
                interrupted = true;
            }
            catch (EmailException e)
            {
                LOGGER.error("Fail in send mail!", e);
            }
            return interrupted;
        }
    }
    
    private final static int EMAIL_QUEUE_TASK_NUM = Integer
        .parseInt(PropertiesUtils.getProperty("email.taskNumber", "20"));
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServerServiceImpl.class);
    
    /**
     * 邮件模板目录
     */
    private static final String MAIL_TEMPLATE_ROOT = "email";
    
    /**
     * 测试邮件消息体
     */
    private static final String TEST_MAIL_CONTENT = "testMailContent.ftl";
    
    /**
     * 测试邮件主题
     */
    private static final String TEST_MAIL_SUBJECT = "testMailSubject.ftl";
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private MailFailedAlarm mailFailedAlarm;
    
    private BlockingQueue<Email> mailQueue = new LinkedBlockingQueue<Email>(1000);
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private ExecutorService taskPool;
    
    @Value("${alarm.testmail.sender.name}")
    private String testSenderName;
    
    @Override
    public void checkMailServer() throws BusinessException
    {
        MailServer mailServer = getMailServer();
        
        // 如果没有设置测试邮箱，则不检测
        if (null == mailServer || StringUtils.isBlank(mailServer.getTestMail()))
        {
            return;
        }
        
        Alarm alarm = new MailFailedAlarm(mailFailedAlarm, mailServer.getServer());
        try
        {
            sendTestMail(mailServer, mailServer.getTestMail(), testSenderName);
            
            alarmHelper.sendRecoverAlarm(alarm);
        }
        catch (Exception e)
        {
            LOGGER.warn("send mail failed.", e);
            alarmHelper.sendAlarm(alarm);
            throw new BusinessException(e);
        }
    }
    
    @PreDestroy
    public void close()
    {
        taskPool.shutdown();
    }
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord record)
    {
        try
        {
            checkMailServer();
        }
        catch (Exception e)
        {
            String message = "check mail server failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message);
            record.setSuccess(false);
            record.setOutput(message);
        }
    }
    
    @Override
    public String getEmailMsgByTemplate(String templateStr, Map<String, Object> messageModel)
        throws IOException
    {
        Configuration cf = FreeMarkers.buildConfiguration(MAIL_TEMPLATE_ROOT);
        cf.setDefaultEncoding("UTF-8");
        Template template = cf.getTemplate(templateStr);
        return FreeMarkers.renderTemplate(template, messageModel);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.system.general.service.MailServerService#getMailServer()
     */
    @Override
    public MailServer getMailServer()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, "mailServer");
        MailServer mailServer = MailServer.buildMailServer(itemList);
        
        if (mailServer != null)
        {
            if (!StringUtils.isBlank(mailServer.getAuthPassword()))
            {
                String realPassword = EDToolsEnhance.decode(mailServer.getAuthPassword(),
                    mailServer.getAuthPasswordKey());
                mailServer.setAuthPassword(realPassword);
            }
        }
        return mailServer;
    }
    
    @PostConstruct
    public void init()
    {
        int nThreads = EMAIL_QUEUE_TASK_NUM;
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        SendMailTask task = null;
        for (int i = 0; i < nThreads; i++)
        {
            task = new SendMailTask();
            taskPool.execute(task);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.system.general.service.MailServerService#saveMailServer(com
     * .huawei.sharedrive.system.general.domain.MailServer)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveMailServer(MailServer mailServer)
    {
        String authPassword = mailServer.getAuthPassword();
        if (!StringUtils.isBlank(authPassword))
        {
            Map<String, String> encodedKeys = EDToolsEnhance.encode(authPassword);
            mailServer.setAuthPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            mailServer.setAuthPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            LOGGER.info("set crypt in isystem.MailServer");
        }
        List<SystemConfig> itemList = mailServer.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null&&systemConfig.getValue()!=null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        if (!StringUtils.isBlank(authPassword))
        {
            mailServer.setAuthPassword(authPassword);
        }
    }
    
    @Override
    public void sendHtmlMail(String to, String cc, String bcc, String subject, String msg)
    {
        HtmlEmail email = new HtmlEmail();
        email.setSubject(subject);
        try
        {
            email.setHtmlMsg(msg);
            // 当收件人邮箱客户端不支持HTML格式邮件时显示文本信息
            email.setTextMsg("Your email client does not support HTML messages");
            email.addTo(to);
            if (StringUtils.isNotBlank(cc))
            {
                email.addCc(cc);
            }
            if (StringUtils.isNotBlank(bcc))
            {
                email.addBcc(bcc);
            }
            mailQueue.put(email);
        }
        catch (EmailException e)
        {
            LOGGER.error("Fail in send mail!", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Fail in send mail!", e);
        }
    }
    
    @Override
    public void sendTestMail(MailServer mailServer, String reciver) throws EmailException, IOException
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        sendTestMail(mailServer, reciver, admin.getName());
    }
    
    @Override
    public void sendTextMail(String to, String cc, String bcc, String subject, String msg)
    {
        Email email = new SimpleEmail();
        email.setSubject(subject);
        try
        {
            email.setMsg(msg);
            email.addTo(to);
            if (StringUtils.isNotBlank(cc))
            {
                email.addCc(cc);
            }
            if (StringUtils.isNotBlank(bcc))
            {
                email.addBcc(bcc);
            }
            mailQueue.put(email);
        }
        catch (EmailException e)
        {
            LOGGER.error("Fail in send mail!", e);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Fail in send mail!", e);
        }
    }
    
    private void sendTestMail(MailServer mailServer, String reciver, String sender) throws EmailException,
        IOException
    {
        if (mailServer == null)
        {
            return;
        }
        Map<String, Object> messageModel = new HashMap<String, Object>(1);
        messageModel.put("userName", sender);
        HtmlEmail email = new HtmlEmail();
        email.setCharset("utf-8");
        email.setHostName(mailServer.getServer());
        String ipSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.getDefault());
        if ("ssl".equals(ipSecurity) || "tls".equals(ipSecurity))
        {
            email.setSSLOnConnect(true);
            email.setSmtpPort(mailServer.getPort());
            email.setSslSmtpPort(String.valueOf(mailServer.getPort()));
        }
        
        else if ("false".equals(ipSecurity))
        {
            email.setSmtpPort(mailServer.getPort());
        }
        else
        {
            LOGGER.error("the type of mailIPSecurity is error:", ipSecurity);
            return;
        }
        if (mailServer.isEnableAuth())
        {
            email.setAuthenticator(new DefaultAuthenticator(mailServer.getAuthUsername(),
                mailServer.getAuthPassword()));
        }
        email.setFrom(mailServer.getSenderMail(), mailServer.getSenderName());
        email.setSubject(getEmailMsgByTemplate(TEST_MAIL_SUBJECT, new HashMap<String, Object>(1)));
        email.setMsg(getEmailMsgByTemplate(TEST_MAIL_CONTENT, messageModel));
        email.addTo(reciver);
        email.setSocketTimeout(20000);
        email.send();
    }
}

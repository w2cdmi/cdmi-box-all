package com.huawei.sharedrive.uam.system.service.impl;

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
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.system.dao.MailServerDao;
import com.huawei.sharedrive.uam.system.domain.HtmlEmailVo;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.FreeMarkers;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.alarm.MailFailedAlarm;

@Component
public class MailServerServiceImpl extends QuartzJobTask implements MailServerService
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MailServerServiceImpl.class);
    
    private BlockingQueue<Email> mailQueue = new LinkedBlockingQueue<Email>(10000);
    
    private ExecutorService taskPool;
    
    @Autowired
    private MailServerDao mailServerDao;
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private MailFailedAlarm mailFailedAlarm;
    
    @Value("${alarm.testmail.sender.name}")
    private String testSenderName;
    
    @PostConstruct
    public void init()
    {
        int nThreads = Constants.EMAIL_QUEUE_TASK_NUM;
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        SendMailTask task;
        for (int i = 0; i < nThreads; i++)
        {
            task = new SendMailTask();
            taskPool.execute(task);
        }
    }
    
    @PreDestroy
    public void close()
    {
        taskPool.shutdown();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.system.general.service.MailServerService#getMailServer()
     */
    @Override
    public MailServer getMailServer(Long id)
    {
        return mailServerDao.get(id);
    }
    
    @Override
    public MailServer getMailServerByAppId(String appId)
    {
        return mailServerDao.getByAppId(appId);
    }
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord record)
    {
        if (checkAllMailServer())
        {
            return;
        }
        String message = "check mail server failed.";
        LOGGER.warn(message);
        record.setSuccess(false);
        record.setOutput(message);
    }
    
    @Override
    public boolean checkAllMailServer()
    {
        List<MailServer> mailServerList = getMailServerList(null, null, null);
        if (null == mailServerList || mailServerList.isEmpty())
        {
            return true;
        }
        
        Alarm alarm;
        
        boolean result = true;
        for (MailServer mailServer : mailServerList)
        {
            alarm = new MailFailedAlarm(mailFailedAlarm, mailServer.getServer());
            try
            {
                if (StringUtils.isBlank(mailServer.getTestMail()))
                {
                    continue;
                }
                
                sendTestMail(mailServer, mailServer.getTestMail(), testSenderName);
                
                alarmHelper.sendRecoverAlarm(alarm);
                
            }
            catch (Exception e)
            {
                LOGGER.warn("send mail failed.", e);
                alarmHelper.sendAlarm(alarm);
                result = false;
            }
        }
        
        return result;
    }
    
    @Override
    public void sendTestMail(Long id, String reciver) throws EmailException, IOException
    {
        MailServer mailServer = mailServerDao.get(id);
        sendTestMail(mailServer, reciver);
    }
    
    @Override
    public void sendTestMail(MailServer mailServer, String reciver) throws EmailException, IOException
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        sendTestMail(mailServer, reciver, admin.getName());
    }
    
    @SuppressWarnings("deprecation")
    private void sendTestMail(MailServer mailServer, String reciver, String sender) throws EmailException,
        IOException
    {
        if (mailServer == null)
        {
            return;
        }
        Map<String, Object> messageModel = new HashMap<String, Object>(1);
        messageModel.put("userName", sender);
        HtmlEmailVo email = new HtmlEmailVo();
        email.setCharset("UTF-8");
        email.setHostName(mailServer.getServer());
        String iPSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.ENGLISH);
        if ("ssl".equals(iPSecurity) || "tls".equals(iPSecurity))
        {
            email.setSSLOnConnect(true);
            email.setSslSmtpPort(mailServer.getPort() + "");
        }
        else if ("false".equals(iPSecurity))
        {
            email.setSmtpPort(mailServer.getPort());
        }
        else
        {
            LOGGER.error("the type of mailIPSecurity is error:", iPSecurity);
            return;
        }
        if (mailServer.isEnableAuth())
        {
            email.setAuthenticator(new DefaultAuthenticator(mailServer.getAuthUsername(),
                mailServer.getAuthPassword()));
        }
        email.setFrom(mailServer.getSenderMail(), mailServer.getSenderName());
        email.setSubject(getEmailMsgByTemplate(Constants.TEST_MAIL_SUBJECT, new HashMap<String, Object>(1)));
        email.setMsg(getEmailMsgByTemplate(Constants.TEST_MAIL_CONTENT, messageModel));
        email.addTo(reciver);
        email.setSendToUsername("test mail");
        email.send();
    }
    
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private class SendMailTask implements Runnable
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    send();
                }
                catch (InterruptedException e)
                {
                    LOGGER.error("Fail in send mail!", e);
                    break;
                }
                catch (Throwable e)
                {
                    LOGGER.error("Fail in send mail!", e);
                }
            }
        }
        
        @SuppressWarnings("deprecation")
        private void send() throws InterruptedException, EmailException
        {
            HtmlEmailVo email = (HtmlEmailVo) mailQueue.take();
            email.setCharset("UTF-8");
            Long mailServerId = email.getMailServerId();
            if (mailServerId == null)
            {
                LOGGER.error("MailServer won't configed!");
                return;
            }
            MailServer mailServer = mailServerDao.get(mailServerId);
            if (mailServer == null)
            {
                LOGGER.error("MailServer won't configed!");
                return;
            }
            email.setHostName(mailServer.getServer());
            String iPSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.ENGLISH);
            if ("ssl".equals(iPSecurity) || "tls".equals(iPSecurity))
            {
                email.setSSLOnConnect(true);
                email.setSslSmtpPort(mailServer.getPort() + "");
            }
            else if ("false".equals(iPSecurity))
            {
                email.setSmtpPort(mailServer.getPort());
            }
            else
            {
                LOGGER.error("the type of mailIPSecurity is error:", iPSecurity);
                return;
            }
            if (mailServer.isEnableAuth())
            {
                email.setAuthenticator(new DefaultAuthenticator(mailServer.getAuthUsername(),
                    mailServer.getAuthPassword()));
            }
            email.setFrom(mailServer.getSenderMail(), mailServer.getSenderName());
            email.send();
        }
    }
    
    @Override
    public String getEmailMsgByTemplate(String templateStr, Map<String, Object> messageModel)
        throws IOException
    {
        Configuration cf = FreeMarkers.buildConfiguration(Constants.MAIL_TEMPLATE_ROOT);
        cf.setDefaultEncoding("UTF-8");
        Template template = cf.getTemplate(templateStr);
        return FreeMarkers.renderTemplate(template, messageModel);
    }
    
    @Override
    public List<MailServer> getMailServerList(MailServer filter, Order order, Limit limit)
    {
        return mailServerDao.getFilterd(filter, order, limit);
    }
    
    @Override
    public void deleteMailServer(Long id)
    {
        mailServerDao.delete(id);
    }
    
    @Override
    public MailServer getDefaultMailServer()
    {
        return mailServerDao.getDefaultMailServer();
    }
    
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public void sendHtmlMail(String shareName, Long mailServerId, String to, String cc, String bcc,
        String subject, String msg)
    {
        HtmlEmailVo email = new HtmlEmailVo();
        email.setSubject(subject);
        try
        {
            email.setSendToUsername(shareName);
            email.setHtmlMsg(msg);
            email.setTextMsg("Your email client does not support HTML messages");
            email.setMailServerId(mailServerId);
            email.addTo(to);
            addParameter(email, cc, bcc);
            mailQueue.put(email);
            LOGGER.info("system send mail to " + shareName + " succeed.");
        }
        catch (EmailException e)
        {
            LOGGER.error("Fail in send mail to " + shareName, e);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Fail in send mail to " + shareName, e);
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public void sendHtmlMail(String shareName, Long mailServerId, String[] to, String[] cc, String subject,
        String msg)
    {
        
        HtmlEmailVo email = new HtmlEmailVo();
        email.setSubject(subject);
        try
        {
            email.setSendToUsername(shareName);
            email.setHtmlMsg(msg);
            email.setMailServerId(mailServerId);
            email.setTextMsg("Your email client does not support HTML messages");
            for (String tmpTo : to)
            {
                email.addTo(tmpTo);
            }
            if (cc != null)
            {
                for (String tmpCc : cc)
                {
                    if (StringUtils.isNotBlank(tmpCc))
                    {
                        email.addCc(tmpCc);
                    }
                }
            }
            mailQueue.put(email);
            LOGGER.info("system send mail to " + shareName + " succeed.");
        }
        catch (EmailException e)
        {
            LOGGER.error("Fail in send mail to " + shareName, e);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Fail in send mail to " + shareName, e);
        }
        
    }
    
    private void addParameter(HtmlEmailVo email, String cc, String bcc) throws EmailException
    {
        if (StringUtils.isNotBlank(cc))
        {
            email.addCc(cc);
        }
        if (StringUtils.isNotBlank(bcc))
        {
            email.addBcc(bcc);
        }
    }

	@Override
	public MailServer getByAccountId(long accountId) {
		// TODO Auto-generated method stub
		return null;
	}
    
}

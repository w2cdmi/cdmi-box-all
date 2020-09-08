package pw.cdmi.box.uam.system.service.impl;

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
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.system.dao.MailServerDao;
import pw.cdmi.box.uam.system.domain.HtmlEmailVo;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.FreeMarkers;

@Component
public class MailServerServiceImpl implements MailServerService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServerServiceImpl.class);
    
    private BlockingQueue<Email> mailQueue = new LinkedBlockingQueue<Email>(1000);
    
    private ExecutorService taskPool;
    
    @Autowired
    private MailServerDao mailServerDao;
    
    @PostConstruct
    public void init()
    {
        int nThreads = Constants.EMAIL_QUEUE_TASK_NUM;
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        SendMailTask sendMailTask = null;
        for (int i = 0; i < nThreads; i++)
        {
            sendMailTask = new SendMailTask();
            taskPool.execute(sendMailTask);
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
        String iPSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.getDefault());
        if ("ssl".equals(iPSecurity) || "tls".equals(iPSecurity))
        {
            email.setSSLOnConnect(true);
            email.setSmtpPort(mailServer.getPort());
            email.setSslSmtpPort(String.valueOf(mailServer.getPort()));
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
        email.send();
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public void sendHtmlMail(Long mailServerId, String to, String cc, String bcc, String subject, String msg)
    {
        HtmlEmailVo email = new HtmlEmailVo();
        email.setSubject(subject);
        try
        {
            email.setHtmlMsg(msg);
            email.setTextMsg("Your email client does not support HTML messages");
            email.setMailServerId(mailServerId);
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
    public void sendHtmlMail(Long mailServerId, String[] to, String[] cc, String subject, String msg)
    {
        HtmlEmailVo email = new HtmlEmailVo();
        email.setSubject(subject);
        try
        {
            email.setHtmlMsg(msg);
            email.setMailServerId(mailServerId);
            email.setTextMsg("Your email client does not support HTML messages");
            fillMailToInfo(to, email);
            fillMailCcInfo(cc, email);
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
    
    private void fillMailCcInfo(String[] cc, HtmlEmailVo email) throws EmailException
    {
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
    }
    
    private void fillMailToInfo(String[] to, HtmlEmailVo email) throws EmailException
    {
        for (String tmpTo : to)
        {
            email.addTo(tmpTo);
        }
    }
    
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private class SendMailTask implements Runnable
    {
        @Override
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
            String iPSecurity = mailServer.getMailSecurity().trim().toLowerCase(Locale.getDefault());
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
    public void saveMailServer(MailServer mailServer)
    {
        mailServer.setId(mailServerDao.getNextAvailableId());
        mailServerDao.create(mailServer);
    }
    
    @Override
    public void updateMailServer(MailServer mailServer)
    {
        mailServerDao.updateMailServer(mailServer);
    }
    
    @Override
    public MailServer getDefaultMailServer()
    {
        return mailServerDao.getDefaultMailServer();
    }
    
}

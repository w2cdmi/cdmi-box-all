/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.scan.thrift;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.plugins.scan.SecurityScanThriftService.Iface;

import pw.cdmi.file.scan.domain.SecurityScanTaskParser;

import com.huawei.sharedrive.thrift.plugins.scan.TSecurityScanTask;

public class SecurityScanThriftServiceImpl implements Iface
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanThriftServiceImpl.class);
    
    public static final String ACCESSKEY_CHANGED_KEY = "accessKeyChanged";
    
    private String jmsUrl;
    
    private String jobQueue;
    
    private Connection connection = null;
    
    private Session session = null;
    
    private MessageProducer jobProducer = null;
    
    @Override
    public void addTask(TSecurityScanTask task) throws TException
    {
        LOGGER.info("Received a security scan task [{}, {}, {}]. ",
            task.getNodeId(),
            task.getObjectId(),
            task.getOwnedBy());
        int priority = task.getPriority();
        byte[] data = SecurityScanTaskParser.tSecurityScanTaskToBytes(task);
        try
        {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(data);
            jobProducer.send(message, DeliveryMode.PERSISTENT, priority, 0);
        }
        catch (JMSException e)
        {
            LOGGER.error("error occur when send convert task", e);
            throw new TException("error occur when send convert task", e);
        }
    }
    
    public void destroy()
    {
        if (session != null)
        {
            try
            {
                session.close();
            }
            catch (JMSException e)
            {
                LOGGER.error("error occur when close session", e);
            }
        }
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (JMSException e)
            {
                LOGGER.error("error occur when close connection", e);
            }
        }
    }
    
    public String getJmsUrl()
    {
        return jmsUrl;
    }
    
    public String getJobQueue()
    {
        return jobQueue;
    }
    
    public void init() throws JMSException
    {
        
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Destination jobDestination = session.createQueue(jobQueue);
        jobProducer = session.createProducer(jobDestination);
        
        LOGGER.info("Security scan Service init successfully");
    }
    
    public void setJmsUrl(String jmsUrl)
    {
        this.jmsUrl = jmsUrl;
    }
    
    public void setJobQueue(String jobQueue)
    {
        this.jobQueue = jobQueue;
    }
    
}

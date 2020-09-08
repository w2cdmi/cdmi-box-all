package com.huawei.sharedrive.app.plugins.scan.manager;

import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTaskParser;
import com.huawei.sharedrive.app.plugins.scan.service.SecurityScanService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.RandomGUID;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.util.Date;

@Component
public class SecurityScanTaskProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanTaskProducer.class);
    
    @Value("${activemq.broker.url}")
    private String jmsUrl;
    
    @Value("${activemq.security.scan.job.queue}")
    private String jobQueue;
    
    private Connection connection = null;
    
    private Session session = null;
    
    private MessageProducer jobProducer = null;
    
    @Autowired
    private SecurityScanService securityScanService;
    
    @PostConstruct
    public void init() throws JMSException
    {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination jobDestination = session.createQueue(jobQueue);
        jobProducer = session.createProducer(jobDestination);
        LOGGER.info("Security scan job producer init successfully");
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void sendKIAScanTask(long nodeId, String nodeName, String objectId, long ownedBy, int dssId,
        int priority)
    {
        SecurityScanTask task = new SecurityScanTask();
        task.setNodeId(nodeId);
        task.setNodeName(nodeName);
        task.setObjectId(objectId);
        task.setOwnedBy(ownedBy);
        task.setDssId(dssId);
        task.setPriority(priority);
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        Date now = new Date();
        task.setCreatedAt(now);
        task.setModifiedAt(now);
        task.setStatus(SecurityScanTask.STATUS_WAIING);
        
        try
        {
            securityScanService.createScanTask(task);
        }
        catch (Exception e)
        {
            LOGGER.error("Create scan task failed!", e);
        }
        
        byte[] data = SecurityScanTaskParser.convertTaskToBytes(task);
        try
        {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(data);
            jobProducer.send(message, DeliveryMode.PERSISTENT, priority, 0);
            LOGGER.info("Send scan task success. Task id:{}, owner:{}, node id:{}, object id:{}, priority:{}",
                task.getTaskId(),
                task.getOwnedBy(),
                task.getNodeId(),
                task.getObjectId(),
                task.getPriority());
        }
        catch (JMSException e)
        {
            LOGGER.error("error occur when send convert task", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR,
                "error occur when send convert task", e);
        }
    }
}

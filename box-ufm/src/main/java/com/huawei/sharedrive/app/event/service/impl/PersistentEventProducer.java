package com.huawei.sharedrive.app.event.service.impl;

import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.domain.PersistentEventParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pw.cdmi.core.exception.InnerException;

import javax.annotation.PostConstruct;
import javax.jms.*;

/**
 * 持久化事件生产者, 事件产生后保存到acitve mq队列
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-5
 * @see
 * @since
 */
@Component
public class PersistentEventProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentEventProducer.class);
    
    @Value("${activemq.broker.url}")
    private String jmsUrl;
    
    @Value("${activemq.persistent.event.queue}")
    private String jobQueue;
    
    private Connection connection = null;
    
    private Session session = null;
    
    private MessageProducer jobProducer = null;
    
    @PostConstruct
    public void init() throws JMSException
    {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination jobDestination = session.createQueue(jobQueue);
        jobProducer = session.createProducer(jobDestination);
        LOGGER.info("Persistent event queue producer init successfully");
    }
    
    public void sendEvent(PersistentEvent event)
    {
        if (event == null)
        {
            LOGGER.warn("Send event failed. Event is null");
            return;
        }
        int priority = event.getPriority() == null ? PersistentEvent.PRIORITY_NORMAL : event.getPriority();
        byte[] data = PersistentEventParser.convertEventToBytes(event);
        try
        {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(data);
            jobProducer.send(message, DeliveryMode.PERSISTENT, priority, 0);
        }
        catch (JMSException e)
        {
            LOGGER.error("Send event failed", e);
            throw new InnerException("Send event failed" + e);
        }
    }
}

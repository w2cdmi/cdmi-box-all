package com.huawei.sharedrive.app.event.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.domain.PersistentEventParser;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.log.LoggerUtil;

@Component
@Lazy(false)
public class PersistentEventDispatcher implements MessageListener
{
    private static final class EventWorker implements Runnable
    {
        private PersistentEventConsumer consumer;
        
        private PersistentEvent event;
        
        EventWorker(PersistentEventConsumer consumer, PersistentEvent event)
        {
            this.consumer = consumer;
            this.event = event;
        }
        
        @Override
        public void run()
        {
            consumer.consumeEvent(event);
        }
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentEventDispatcher.class);
    
    private static final int MAX_WORKER_NUM = Integer
        .parseInt(PropertiesUtils.getProperty("persistent.event.execute.workers", "20"));
        
    private Map<EventType, List<PersistentEventConsumer>> consumerMap = new HashMap<EventType, List<PersistentEventConsumer>>(
        BusinessConstants.INITIAL_CAPACITIES);
        
    @Value("${activemq.broker.url}")
    private String jmsUrl;
    
    @Value("${activemq.persistent.event.queue}")
    private String jobQueue;
    
    private Connection connection = null;
    
    private Session session = null;
    
    private ExecutorService executorService;
    
    public void addConsumer(PersistentEventConsumer consumer)
    {
        EventType[] types = consumer.getInterestedEvent();
        if (types == null)
        {
            return;
        }
        
        List<PersistentEventConsumer> list = null;
        for (EventType type : types)
        {
            if (consumerMap.containsKey(type))
            {
                consumerMap.get(type).add(consumer);
            }
            else
            {
                list = new LinkedList<PersistentEventConsumer>();
                list.add(consumer);
                consumerMap.put(type, list);
            }
        }
        LOGGER.info("Add consumer {}, Interested event: {}",
            consumer.toString(),
            consumer.getInterestedEvent());
    }
    
    @PostConstruct
    public void init() throws JMSException
    {
        LOGGER.info("Persistent event listener init");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        
        MessageConsumer consumer = null;
        Destination destination = null;
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(jobQueue);
        consumer = session.createConsumer(destination);
        consumer.setMessageListener(this);
        executorService = new ThreadPoolExecutor(MAX_WORKER_NUM, MAX_WORKER_NUM, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000));
        
        LOGGER.info("Persistent event init completed. Worker number: {}", MAX_WORKER_NUM);
    }
    
    @Override
    public void onMessage(Message message)
    {
        LoggerUtil.regiestThreadLocalLog();
        
        if (message instanceof BytesMessage)
        {
            try
            {
                PersistentEvent event = parseMessage(message);
                if (event != null)
                {
                    LOGGER.info("Receive a persistent event: {}", event.toString());
                    dispatchEvent(event);
                }
            }
            catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
            
        }
        
    }
    
    private void dispatchEvent(PersistentEvent event)
    {
        EventWorker worker = null;
        List<PersistentEventConsumer> list = consumerMap.get(event.getEventType());
        if (list != null)
        {
            for (PersistentEventConsumer consumer : list)
            {
                worker = new EventWorker(consumer, event);
                try
                {
                    executorService.execute(worker);
                }
                catch (RejectedExecutionException e)
                {
                    LOGGER.warn("execute EventWorker failed.", e);
                }
            }
        }
    }
    
    private PersistentEvent parseMessage(Message message)
    {
        PersistentEvent event = null;
        if (message instanceof BytesMessage)
        {
            BytesMessage bytesMessage = (BytesMessage) message;
            try
            {
                byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(data);
                event = PersistentEventParser.bytesToPersistentEvent(data, 0, data.length);
            }
            catch (Exception e)
            {
                LOGGER.warn("Parser message failed.", e);
            }
        }
        return event;
    }
    
}

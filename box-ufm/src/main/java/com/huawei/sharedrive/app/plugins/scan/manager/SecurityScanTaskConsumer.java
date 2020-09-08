package com.huawei.sharedrive.app.plugins.scan.manager;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginServiceClusterService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Component
@Lazy(false)
public class SecurityScanTaskConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanTaskConsumer.class);
    
    @Value("${activemq.broker.url}")
    private String jmsUrl;
    
    @Value("${activemq.security.scan.job.queue}")
    private String jobQueue;
    
    @Autowired
    private PluginServiceClusterService pluginServiceClusterService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    private Connection connection = null;
    
    private static final int MAX_WORKER_NUM = Integer.parseInt(PropertiesUtils.getProperty("security.scan.job.queue.consumers",
        "5"));
    
    @PostConstruct
    public void init() throws JMSException
    {
        
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        SecurityScanTaskListener listener = null;
        
        for (int i = 0; i < MAX_WORKER_NUM; i++)
        {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(jobQueue);
            consumer = session.createConsumer(destination);
            listener = new SecurityScanTaskListener(pluginServiceClusterService, resourceGroupService,
                dssDomainService);
            consumer.setMessageListener(listener);
        }
        
        LOGGER.info(">>>>>> Security scan task consumer init successfully");
    }
    
}

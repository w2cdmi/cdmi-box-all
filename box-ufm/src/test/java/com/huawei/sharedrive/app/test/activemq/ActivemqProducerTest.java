package com.huawei.sharedrive.app.test.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActivemqProducerTest
{
    
    public static void main(String[] args) throws Exception
    {
        String jmsUrl = "failover:(tcp://10.169.52.72:61616,tcp://10.169.100.244:61616,tcp://10.183.36.164:61616)?jms.messagePrioritySupported=true";
        String jobQueue = "onebox.preview.convert.ufm.job.queue";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination jobDestination = session.createQueue(jobQueue);
        MessageProducer jobProducer = session.createProducer(jobDestination);
        TextMessage message = session.createTextMessage("this is a test.");
        jobProducer.send(message);
        connection.close();
    }
    
}

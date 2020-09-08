package com.huawei.sharedrive.app.test.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActivemqConsumerTest
{
    private static class MyMessageListener implements MessageListener
    {
        @Override
        public void onMessage(Message message)
        {
            System.out.println(message);
        }
        
    }
    
    public static void main(String[] args) throws Exception
    {
        String jmsUrl = "failover:(tcp://10.169.52.72:61616,tcp://10.169.100.244:61616,tcp://10.183.36.164:61616)?jms.messagePrioritySupported=true";
        String jobQueue = "onebox.preview.convert.ufm.job.queue";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(jobQueue);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MyMessageListener());
        System.out.println("dfsfsfdf");
    }
    
}

package com.huawei.sharedrive.app.message.mq.producer;

import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;

import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageParser;

public class TopicProducer
{
    private JmsTemplate template;
    
    private Topic destination;
    
    public Topic getDestination()
    {
        return destination;
    }
    
    public JmsTemplate getTemplate()
    {
        return template;
    }
    
    public void send(Message message)
    {
        if (template == null)
        {
            throw new IllegalStateException("template is not setted");
        }
        if (destination == null)
        {
            throw new IllegalStateException("destination is not setted");
        }
        byte[] data = MessageParser.convertMessageToBytes(message);
        template.convertAndSend(this.destination, data);
    }
    
    public void setDestination(Topic destination)
    {
        this.destination = destination;
    }
    
    public void setTemplate(JmsTemplate template)
    {
        this.template = template;
    }
}

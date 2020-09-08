package com.huawei.sharedrive.app.message.mq.producer;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

public class QueueProducer
{
    private JmsTemplate template;
    
    private Queue destination;
    
    public JmsTemplate getTemplate()
    {
        return template;
    }
    
    public void setTemplate(JmsTemplate template)
    {
        this.template = template;
    }
    
    public Queue getDestination()
    {
        return destination;
    }
    
    public void setDestination(Queue destination)
    {
        this.destination = destination;
    }
    
    public void send(String message)
    {
        if (template == null)
        {
            throw new IllegalStateException("template is not setted");
        }
        if (destination == null)
        {
            throw new IllegalStateException("destination is not setted");
        }
        template.convertAndSend(this.destination, message);
    }
    
}

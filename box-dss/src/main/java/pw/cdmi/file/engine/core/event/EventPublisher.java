/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.file.engine.core.event;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import pw.cdmi.file.engine.core.job.ThreadPool;

/**
 * 事件发布器
 * 
 * @author s90006125
 * 
 */
@Component
public class EventPublisher implements ApplicationEventPublisherAware
{
    
    private static ApplicationEventPublisher appEventPublisher;
    
    private static void setEventPublisher(ApplicationEventPublisher publisher)
    {
        appEventPublisher = publisher;
    }
    
    /** 用于保存异步事件的队列 */
    private static BlockingQueue<AbstractEvent> eventQueue = new LinkedBlockingQueue<AbstractEvent>();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);
    
    public static void publishEvent(AbstractEvent event)
    {
        if (null == event)
        {
            return;
        }
        LOGGER.info("Publish Event [ " + event + " ] ");
        if (event.isAsync())
        {
            publishToAsyncQueue(event);
        }
        else
        {
            appEventPublisher.publishEvent(event);
        }
    }
    
    public static void publishEvent(List<AbstractEvent> events)
    {
        if (null == events)
        {
            return;
        }
        
        // 先讲所有异步事件放到异步队列，使其开始执行，然后再处理同步事件
        for (AbstractEvent event : events)
        {
            if (event.isAsync())
            {
                publishToAsyncQueue(event);
            }
        }
        
        // 处理同步事件
        for (AbstractEvent event : events)
        {
            if (!event.isAsync())
            {
                appEventPublisher.publishEvent(event);
            }
        }
    }
    
    /**
     * 放入异步队列
     * 
     * @param event
     */
    private static void publishToAsyncQueue(AbstractEvent event)
    {
        try
        {
            eventQueue.put(event);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("PublistEvent [ " + event + " ] Failed.", e);
        }
    }
    
    @PostConstruct
    public void init()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    takeAndPublishAsynEventWithoutException();
                }
                
            }

            private void takeAndPublishAsynEventWithoutException()
            {
                try
                {
                    AbstractEvent event = eventQueue.take();
                    LOGGER.debug("Take Event [ " + event + " ]");
                    publishAsyncEvent(event);
                }
                catch (Exception e)
                {
                    LOGGER.warn("Wait For Event Failed.", e);
                }
            }
        }).start();
    }
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        EventPublisher.setEventPublisher(applicationEventPublisher);
    }
    
    /**
     * 处理异步事件
     * 
     * @param event
     */
    private void publishAsyncEvent(final AbstractEvent event)
    {
        ThreadPool.execute(new PublishEventThread(appEventPublisher, event));
    }
    
    private static final class PublishEventThread implements Runnable
    {
        private ApplicationEventPublisher eventPublisher;
        
        private AbstractEvent event;
        
        PublishEventThread(ApplicationEventPublisher eventPublisher, AbstractEvent event)
        {
            this.eventPublisher = eventPublisher;
            this.event = event;
        }
        @Override
        public void run()
        {
            this.eventPublisher.publishEvent(this.event);
        }
    }
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import pw.cdmi.file.engine.core.job.ThreadPool;
/**
 * 事件分发器<br>
 * 负责在收到事件之后，将其根据事件类型，分发到不同的处理器上
 * 
 * @author s90006125
 * 
 */
public class EventDispatcher implements ApplicationListener<AbstractEvent>
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);
    
    /** 事件监听器集合 */
    private Map<Class<?>, List<AbstractEventListener<AbstractEvent>>> eventListeners = new HashMap<Class<?>, List<AbstractEventListener<AbstractEvent>>>(1);
    
    /** 用于保存异步事件的队列 */
    private BlockingQueue<AbstractEvent> eventQueue = new LinkedBlockingQueue<AbstractEvent>();
    
    @PostConstruct
    public void init()
    {
        // 启动一个线程，来处理异步队列
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    takeAndHandleAsynEventWithoutException();
                }
            }

            private void takeAndHandleAsynEventWithoutException()
            {
                try
                {
                    AbstractEvent event = eventQueue.take();
                    LOGGER.debug("Take event for name: " + event);
                    handleAsyncEvent(event);
                }
                catch (Exception e)
                {
                    LOGGER.error("Wait For Event Failed", e);
                }
            }
        }).start();
    }
    
    @Override
    public void onApplicationEvent(AbstractEvent event)
    {
        LOGGER.info("Recive Event [ " + event + " ]");
        if (event.isAsync())
        {
            publishToAsyncQueue(event);
        }
        else
        {
            dispatcherEvent(event);
        }
        
        LOGGER.info("End Handle Event [ " + event + " ] Handle.");
    }
    
    public void setEventListeners(List<AbstractEventListener<AbstractEvent>> eventListeners)
    {
        List<AbstractEventListener<AbstractEvent>> temp = null;
        for (AbstractEventListener<AbstractEvent> listener : eventListeners)
        {
            temp = this.eventListeners.get(listener.getSupportType());
            
            if (null == temp)
            {
                temp = new ArrayList<AbstractEventListener<AbstractEvent>>(1);
                this.eventListeners.put(listener.getSupportType(), temp);
            }
            temp.add(listener);
        }
    }
    
    /**
     * 采用同步方式处理事件
     * 
     * @param event
     */
    private <T extends AbstractEvent> void dispatcherEvent(T event)
    {
        // 获取能够处理该事件的所有监听器
        List<AbstractEventListener<AbstractEvent>> listeners = eventListeners.get(event.getClass());
        
        if (null == listeners || listeners.isEmpty())
        {
            return;
        }
        // 处理事件
        for (AbstractEventListener<AbstractEvent> listener : listeners)
        {
            listener.onApplicationEvent(event);
        }
    }
    
    /**
     * 处理异步事件
     * 
     * @param event
     */
    private void handleAsyncEvent(final AbstractEvent event)
    {
        ThreadPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                dispatcherEvent(event);
            }
        });
    }
    
    /**
     * 放入异步队列
     * 
     * @param event
     */
    private void publishToAsyncQueue(AbstractEvent event)
    {
        try
        {
            eventQueue.put(event);
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("PublistEvent [ " + event + " ] Failed.", e);
        }
    }
}

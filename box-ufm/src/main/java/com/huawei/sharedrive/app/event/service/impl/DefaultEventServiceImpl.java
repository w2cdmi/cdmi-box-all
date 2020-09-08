/**
 * 
 */
package com.huawei.sharedrive.app.event.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * @author q90003805
 *         
 */
public class DefaultEventServiceImpl implements EventService, InitializingBean
{
    private static final class EventConsumerWrapper implements Runnable
    {
        private EventConsumer consumer;
        
        private Event event;
        
        EventConsumerWrapper(EventConsumer consumer, Event event)
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
    
    private static final int DEFAULT_CAPACITY = 100000;
    
    private static final int DEFAULT_MAX_CONSUMER_WORKERS = 10;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventServiceImpl.class);
    
    private static final int WARN_SIZE_QUEUE = 20000;
    
    private int capacity = DEFAULT_CAPACITY;
    
    private Map<EventType, List<EventConsumer>> consumerMap = new HashMap<EventType, List<EventConsumer>>(
        BusinessConstants.INITIAL_CAPACITIES);
        
    private ExecutorService executorService;
    
    private int maxConsumerWorkers = DEFAULT_MAX_CONSUMER_WORKERS;
    
    private LinkedBlockingQueue<Runnable> queue = null;
    
    @Override
    public void afterPropertiesSet() throws BaseRunException
    {
        queue = new LinkedBlockingQueue<Runnable>(capacity);
        executorService = new ThreadPoolExecutor(maxConsumerWorkers, maxConsumerWorkers, 0L,
            TimeUnit.MILLISECONDS, queue);
    }
    
    public void destroy()
    {
        executorService.shutdown();
    }
    
    @Override
    public void fireEvent(Event event)
    {
        if (event == null)
        {
            return;
        }
        int size = queue.size();
        if (size > WARN_SIZE_QUEUE)
        {
            LOGGER.warn("The block queue size is " + size);
        }
        List<EventConsumer> list = consumerMap.get(event.getType());
        if (list != null)
        {
            EventConsumerWrapper wrapper = null;
            for (EventConsumer eventConsumer : list)
            {
                wrapper = new EventConsumerWrapper(eventConsumer, event);
                executorService.execute(wrapper);
            }
        }
        
    }
    
    public int getCapacity()
    {
        return capacity;
    }
    
    public int getMaxConsumerWorkers()
    {
        return maxConsumerWorkers;
    }
    
    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }
    
    public void setConsumers(List<EventConsumer> consumers)
    {
        EventType[] types = null;
        for (EventConsumer eventConsumer : consumers)
        {
            types = eventConsumer.getInterestedEvent();
            if (types == null)
            {
                break;
            }
            for (EventType type : types)
            {
                if (consumerMap.containsKey(type))
                {
                    consumerMap.get(type).add(eventConsumer);
                }
                else
                {
                    List<EventConsumer> list = new LinkedList<EventConsumer>();
                    list.add(eventConsumer);
                    consumerMap.put(type, list);
                }
            }
        }
    }
    
    public void setMaxConsumerWorkers(int maxConsumerWorkers)
    {
        this.maxConsumerWorkers = maxConsumerWorkers;
    }
}

package pw.cdmi.box.uam.event.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;

import pw.cdmi.box.uam.event.domain.Event;
import pw.cdmi.box.uam.event.domain.EventType;
import pw.cdmi.box.uam.event.service.EventConsumer;
import pw.cdmi.box.uam.event.service.EventService;
import pw.cdmi.box.uam.util.BusinessConstants;

public class DefaultEventServiceImpl implements EventService, InitializingBean
{
    
    private static final int DEFAULT_CAPACITY = 10000;
    
    private static final int DEFAULT_MAX_CONSUMER_WORKERS = 10;
    
    private int capacity = DEFAULT_CAPACITY;
    
    private Map<EventType, List<EventConsumer>> consumerMap = new HashMap<EventType, List<EventConsumer>>(
        BusinessConstants.INITIAL_CAPACITIES);
        
    private ExecutorService executorService;
    
    private int maxConsumerWorkers = DEFAULT_MAX_CONSUMER_WORKERS;
    
    private LinkedBlockingQueue<Runnable> queue = null;
    
    @Override
    public void afterPropertiesSet()
    {
        queue = new LinkedBlockingQueue<Runnable>(capacity);
        executorService = new ThreadPoolExecutor(maxConsumerWorkers, maxConsumerWorkers, 0L,
            TimeUnit.MILLISECONDS, queue);
    }
    
    public void destroy()
    {
        if (executorService == null)
        {
            return;
        }
        executorService.shutdown();
    }
    
    @Override
    public void fireEvent(Event event)
    {
        if (executorService == null)
        {
            return;
        }
        if (event == null)
        {
            return;
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
        EventType[] types;
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

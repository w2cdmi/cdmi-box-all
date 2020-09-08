package com.huawei.sharedrive.uam.event.service.impl;

import com.huawei.sharedrive.uam.event.domain.Event;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.event.service.EventConsumer;
import com.huawei.sharedrive.uam.event.service.EventService;
import com.huawei.sharedrive.uam.util.BusinessConstants;

import pw.cdmi.common.deamon.DeamonService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultEventServiceImpl implements EventService
{
    private Map<EventType, List<EventConsumer>> consumerMap = new HashMap<EventType, List<EventConsumer>>(BusinessConstants.INITIAL_CAPACITIES);
        
    private DeamonService executeService;

    public DeamonService getExecuteService() {
        return executeService;
    }

    public void setExecuteService(DeamonService executeService) {
        this.executeService = executeService;
    }

    @Override
    public void fireEvent(Event event)
    {
        if (executeService == null)
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
            EventConsumerWrapper wrapper;
            for (EventConsumer eventConsumer : list)
            {
                wrapper = new EventConsumerWrapper(eventConsumer, event);
                executeService.execute(wrapper);
            }
        }
    }
    
    public void setConsumers(List<EventConsumer> consumers)
    {
        EventType[] types;
        for (EventConsumer eventConsumer : consumers)
        {
            types = eventConsumer.getInterestedEvent();
            if (types == null || types.length == 0)
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
}

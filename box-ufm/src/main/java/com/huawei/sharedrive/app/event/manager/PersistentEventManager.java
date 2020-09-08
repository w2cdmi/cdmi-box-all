package com.huawei.sharedrive.app.event.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.event.service.PersistentEventService;

@Component
public class PersistentEventManager
{
    @Autowired
    private PersistentEventService persistentEventService;
    
    public void fireEvent(PersistentEvent event)
    {
        persistentEventService.fireEvent(event);
    }
    
    public void registerConsumer(PersistentEventConsumer consumer)
    {
        persistentEventService.registerConsumer(consumer);
    }
    
}

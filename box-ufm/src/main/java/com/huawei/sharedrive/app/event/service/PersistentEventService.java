package com.huawei.sharedrive.app.event.service;

import com.huawei.sharedrive.app.event.domain.PersistentEvent;

public interface PersistentEventService
{
    void fireEvent(PersistentEvent event);
    
    void registerConsumer(PersistentEventConsumer consumer);
}

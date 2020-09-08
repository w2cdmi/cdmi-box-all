package com.huawei.sharedrive.uam.event.service;

import com.huawei.sharedrive.uam.event.domain.Event;
import com.huawei.sharedrive.uam.event.domain.EventType;

public interface EventConsumer
{
    void consumeEvent(Event event);
    
    EventType[] getInterestedEvent();
}

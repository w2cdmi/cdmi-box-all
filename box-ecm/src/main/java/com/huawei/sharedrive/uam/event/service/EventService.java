package com.huawei.sharedrive.uam.event.service;

import com.huawei.sharedrive.uam.event.domain.Event;

public interface EventService
{
    
    void fireEvent(Event event);
    
}

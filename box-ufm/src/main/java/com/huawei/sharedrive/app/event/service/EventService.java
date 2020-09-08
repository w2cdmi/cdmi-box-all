package com.huawei.sharedrive.app.event.service;

import com.huawei.sharedrive.app.event.domain.Event;

/**
 * 
 * @author q90003805
 * 
 */
public interface EventService
{
    
    /**
     * 产生一个事件
     * 
     * @param event 事件对象
     */
    void fireEvent(Event event);
    
}

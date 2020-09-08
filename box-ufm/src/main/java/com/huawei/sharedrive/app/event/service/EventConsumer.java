/**
 * 
 */
package com.huawei.sharedrive.app.event.service;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;

/**
 * @author q90003805
 * 
 */
public interface EventConsumer
{
    /**
     * 消费一个事件
     * 
     * @param event
     */
    void consumeEvent(Event event);
    
    /**
     * 获取需要监听的事件类型，
     * 
     * @return
     */
    EventType[] getInterestedEvent();
}

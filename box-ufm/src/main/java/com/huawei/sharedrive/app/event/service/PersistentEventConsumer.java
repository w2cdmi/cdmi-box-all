package com.huawei.sharedrive.app.event.service;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;

public interface PersistentEventConsumer
{
    /**
     * 消费事件
     * 
     * @param event
     */
    void consumeEvent(PersistentEvent event);
    
    /**
     * 获取需要监听的事件类型的集合
     * 
     * @return
     */
    EventType[] getInterestedEvent();
    
}

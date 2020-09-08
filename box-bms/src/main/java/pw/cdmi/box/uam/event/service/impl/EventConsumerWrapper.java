/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.event.service.impl;

import pw.cdmi.box.uam.event.domain.Event;
import pw.cdmi.box.uam.event.service.EventConsumer;

/**
 * 
 * @author s90006125
 *
 */
public final class EventConsumerWrapper implements Runnable
{
    private EventConsumer consumer;
    
    private Event event;
    
    public EventConsumerWrapper(EventConsumer consumer, Event event)
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

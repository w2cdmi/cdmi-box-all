package pw.cdmi.box.disk.event.service;

import pw.cdmi.box.disk.event.domain.Event;
import pw.cdmi.box.disk.event.domain.EventType;

public interface EventConsumer
{
    void consumeEvent(Event event);
    
    EventType[] getInterestedEvent();
}

package pw.cdmi.box.uam.event.service;

import pw.cdmi.box.uam.event.domain.Event;
import pw.cdmi.box.uam.event.domain.EventType;

public interface EventConsumer
{
    void consumeEvent(Event event);
    
    EventType[] getInterestedEvent();
}

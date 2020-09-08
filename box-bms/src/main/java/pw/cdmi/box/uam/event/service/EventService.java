package pw.cdmi.box.uam.event.service;

import pw.cdmi.box.uam.event.domain.Event;

public interface EventService
{
    
    void fireEvent(Event event);
    
}

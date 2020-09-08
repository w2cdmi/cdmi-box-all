package pw.cdmi.box.disk.event.service;

import pw.cdmi.box.disk.event.domain.Event;

public interface EventService
{
    
    void fireEvent(Event event);
    
}

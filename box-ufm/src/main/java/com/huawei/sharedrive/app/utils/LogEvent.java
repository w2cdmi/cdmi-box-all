package com.huawei.sharedrive.app.utils;

import java.util.Date;

import org.slf4j.Logger;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public final class LogEvent
{
    private LogEvent()
    {
    }
    
    public static void createEvent(UserToken userToken, EventType type, INode srcNode,
        EventService eventService, Logger logger)
    {
        try
        {
            Event event = new Event(userToken);
            event.setType(type);
            event.setSource(srcNode);
            event.setCreatedAt(new Date());
            event.setCreatedBy(userToken.getId());
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            logger.error(e.toString(), e);
        }
    }
}

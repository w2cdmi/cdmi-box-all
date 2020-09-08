package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.impl.DefaultEventServiceImpl;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.utils.LogEvent;

public class LogEventTest
{
    @Test
    public void createEventTest()
    {
        Logger logger = LoggerFactory.getLogger(LogEventTest.class);
        LogEvent.createEvent(new UserToken(),
            EventType.SHARE_CREATE,
            new INode(),
            new DefaultEventServiceImpl(),
            logger);
    }
    
    @Test
    public void createEventTest1()
    {
        try
        {
            Logger logger = LoggerFactory.getLogger(LogEventTest.class);
            LogEvent.createEvent(new UserToken(), EventType.SHARE_CREATE, new INode(), null, logger);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

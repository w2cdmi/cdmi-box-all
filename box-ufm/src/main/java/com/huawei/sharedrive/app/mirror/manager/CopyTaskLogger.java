package com.huawei.sharedrive.app.mirror.manager;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.user.domain.User;

@Service("copyTaskLogger")
@Lazy(false)
public class CopyTaskLogger
{
    
    @Autowired
    private EventService eventService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskLogger.class);
    
    public void sendCopyEvent(CopyTask task)
    {
        try
        {
            UserToken user = new UserToken();
            user.setId(User.SYSTEM_USER_ID);
            Event event = new Event(user);
            event.setSource(null);
            event.setCreatedAt(new Date());
            event.setCreatedBy(user.getId());
            event.setType(EventType.MIRROR_COPY_OBJECT_SUCCESS);
            event.setOptType(UserLogType.MIRROR_COPY_OBJECT_SUCCESS);
            event.setParams(task.getTaskStr().split(","));
            event.setKeyword(MirrorCommonStatic.MIRROR_COPY_OBJECT_SUCCESS_KEY);
            event.setUserToken(user);
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
}

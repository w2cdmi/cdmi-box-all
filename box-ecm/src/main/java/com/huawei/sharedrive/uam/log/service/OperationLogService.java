package com.huawei.sharedrive.uam.log.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.event.domain.Event;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.event.service.EventConsumer;
import com.huawei.sharedrive.uam.log.domain.FullViewLogID;

import pw.cdmi.core.utils.UUIDUtils;

@Service
public class OperationLogService implements EventConsumer
{
    
    private static ThreadLocal<Map<String, String>> logThreadLocal = new ThreadLocal<Map<String, String>>();
    
    @Value("${log.fullview.innerid}")
    private String innerID;
    
    /**
     * 
     * @param fullViewLogID
     * @return
     */
    public String appendID(String sourceLogID)
    {
        FullViewLogID logID = new FullViewLogID(sourceLogID);
        logID.appendID(getGourpID(), innerID, new Date());
        String fullViewLogID = logID.toString();
        logThreadLocal.get().put("FullViewLogID", fullViewLogID);
        return fullViewLogID;
    }
    
    @Override
    public void consumeEvent(Event event)
    {
    }
    
    public String generateFullViewLogID()
    {
        String randomId = UUIDUtils.getValueAfterMD5().substring(0, 16);
        FullViewLogID logID = new FullViewLogID(randomId, getGourpID(), innerID, new Date());
        String fullViewLogID = logID.toString();
        logThreadLocal.get().put("FullViewLogID", fullViewLogID);
        return fullViewLogID;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.event.service.EventConsumer#getInterestedEvent()
     */
    @Override
    public EventType[] getInterestedEvent()
    {
        // TODO Auto-generated method stub
        return new EventType[]{};
    }
    
    public String mergeID(String dest, String src)
    {
        return null;
    }
    
    /**
     * 
     */
    public void recordLog()
    {
    }
    
    public void recordLog(String message)
    {
    }
    
    private String getGourpID()
    {
        String groupID = "00";
        return groupID;
    }
    
}

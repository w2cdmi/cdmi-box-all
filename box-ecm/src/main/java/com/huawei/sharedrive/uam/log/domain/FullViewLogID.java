package com.huawei.sharedrive.uam.log.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.uam.util.BusinessConstants;

public class FullViewLogID
{
    private List<AppendID> appendIDs = new ArrayList<AppendID>(BusinessConstants.INITIAL_CAPACITIES);
    
    private String headID;
    
    public FullViewLogID(final String logID)
    {
        if (logID == null || logID.length() < 32 || logID.length() % 16 != 0)
        {
            throw new IllegalArgumentException("Invalidate LogID:" + logID);
        }
        
        headID = logID.substring(0, 16);
        String append = logID.substring(16);
        
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        while (append.length() > 0)
        {
            append = tryAppendID(append, format);
        }
    }

    private String tryAppendID(String append, SimpleDateFormat format)
    {
        String groupID;
        String innerID;
        String dateStr;
        String appendID = append.substring(0, 16);
        append = append.substring(16);
        
        groupID = appendID.substring(0, 2);
        innerID = appendID.substring(2, 4);
        dateStr = appendID.substring(4);
        try
        {
            appendID(groupID, innerID, format.parse(dateStr));
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Invalidate date format:" + dateStr, e);
        }
        return append;
    }
    
    public FullViewLogID(final String headID, final String groupID, final String innerID, final Date date)
    {
        if (headID == null || headID.length() != 16)
        {
            throw new IllegalArgumentException("Invalidate headID:" + headID);
        }
        this.headID = headID;
        appendID(groupID, innerID, date);
    }
    
    public static FullViewLogID parse(String logID)
    {
        return new FullViewLogID(logID);
    }
    
    /**
     * @param appendIDs the appendIDs to set
     */
    public void appendID(String groupID, String innerID, Date date)
    {
        if (groupID == null || groupID.length() != 2)
        {
            throw new IllegalArgumentException("Invalidate GroupID:" + groupID);
        }
        if (innerID == null || innerID.length() != 2)
        {
            throw new IllegalArgumentException("Invalidate InnerID:" + innerID);
        }
        if (date == null)
        {
            throw new IllegalArgumentException("Date should be null");
        }
        appendIDs.add(new AppendID(groupID, innerID, date));
    }
    
    /**
     * @return the appendIDs
     */
    public List<AppendID> getAppendIDs()
    {
        return Collections.unmodifiableList(appendIDs);
    }
    
    /**
     * @return the headID
     */
    public String getHeadID()
    {
        return headID;
    }
    
    @Override
    public String toString()
    {
        StringBuilder fullViewLogID = new StringBuilder();
        fullViewLogID.append(headID);
        for (AppendID appendID : appendIDs)
        {
            fullViewLogID.append(appendID.toString());
        }
        return fullViewLogID.toString();
    }
}
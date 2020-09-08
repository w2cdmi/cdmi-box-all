package com.huawei.sharedrive.app.log.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppendID
{
    private Date date;
    
    private String groupID;
    
    private String innerID;
    
    AppendID(String groupID, String innerID, Date date)
    {
        this.groupID = groupID;
        this.innerID = innerID;
        if (date == null)
        {
            this.date = null;
        }
        else
        {
            this.date = (Date) date.clone();
        }
    }
    
    /**
     * @return the date
     */
    public Date getDate()
    {
        if (date == null)
        {
            return null;
        }
        return (Date) date.clone();
    }
    
    /**
     * @return the groupID
     */
    public String getGroupID()
    {
        return groupID;
    }
    
    /**
     * @return the innerID
     */
    public String getInnerID()
    {
        return innerID;
    }
    
    @Override
    public String toString()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        return groupID + innerID + format.format(date);
    }
    
}
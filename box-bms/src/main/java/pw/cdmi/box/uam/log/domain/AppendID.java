package pw.cdmi.box.uam.log.domain;

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
        this.date = (date == null ? null : (Date) date.clone());
    }
    
    /**
     * @return the date
     */
    public Date getDate()
    {
        return date == null ? null : (Date) date.clone();
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
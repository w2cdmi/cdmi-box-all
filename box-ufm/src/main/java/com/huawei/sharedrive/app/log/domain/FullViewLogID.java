package com.huawei.sharedrive.app.log.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * @author c90006080
 * 
 * @描述：全流程日志ID
 * @示例：WpohB+Hh8qJdhfw6A1131209095121D2131209095312
 * @长度：变长字符串
 * @取值范围：Base64字符
 * @详细描述: FullViewLogID(全局日志ID，对外称为RequestID)是一个变长字符串，由一个HeadID和一个或者多个AppendID构成：
 *        FullViewLogID=${HeadID} ${AppendID}+
 * 
 *        HeadID 日志主ID， 写入到日志文件的ID, 可以通过该ID 在日志文件过滤： HeadID=[Base64字符]{16}
 * 
 *        AppendID 日志追加ID， 用于日志定位，可以通过该ID 查找到日志记录位置，有一个NodeID 和Date 构成：
 *        AppendID=${NodeID}${Date}
 * 
 *        NodeID 记录日志节点ID，用于确定记录日志节点位置，有GroupID和InnerID构成： NodeID=${GroupID}${InnerID}
 * 
 *        GroupID 节点所在资源组ID、也称为DC ID. 每一个DataServer集群被称为一个DC(DataCenter),或者资源组。
 *        该ID是在AppServer注册DC时分配的。AppServer 集群的ID 为“00”： GourpID=[Base64字符]{2}
 * 
 *        InnerID 节点资源组（DC）内ID，自动部署时分配： InnerID=[0-9]{2}
 * 
 *        Date 记录日志时间，可用于确定日志记录的文件（日志分片记录和转储后），以及确定日志收集范围： Date=[0-9]{12}
 */
public class FullViewLogID
{
    private List<AppendID> appendIDs = new ArrayList<AppendID>(BusinessConstants.INITIAL_CAPACITIES);
    
    private String headID;
    
    public FullViewLogID(final String logID)
    {
        // 长度检查
        if (logID == null || logID.length() < 32 || logID.length() % 16 != 0)
        {
            throw new IllegalArgumentException("Invalidate LogID:" + logID);
        }
        
        headID = logID.substring(0, 16);
        String append = logID.substring(16);
        
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        String appendID = null;
        String groupID = null;
        String innerID = null;
        String dateStr = null;
        boolean isContinue = append.length() > 0;
        Date date = null;
        try
        {
            while (isContinue)
            {
                appendID = append.substring(0, 16);
                append = append.substring(16);
                groupID = appendID.substring(0, 2);
                innerID = appendID.substring(2, 4);
                dateStr = appendID.substring(4);
                date = format.parse(dateStr);
                appendID(groupID, innerID, date);
                isContinue = append.length() > 0;
            }
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Invalidate date format:" + dateStr, e);
        }
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
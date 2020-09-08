package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 消息列举响应对象
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-3-19
 * @see
 * @since
 */
public class AnnouncementList implements Serializable
{
    private static final long serialVersionUID = -1819854091196782648L;
    
    private Long offset;
    
    private Integer limit;
    
    private long totalCount;
    
    private List<AnnouncementResponse> announcements;
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public List<AnnouncementResponse> getAnnouncements()
    {
        return announcements;
    }
    
    public void setAnnouncements(List<AnnouncementResponse> announcements)
    {
        this.announcements = announcements;
    }
}

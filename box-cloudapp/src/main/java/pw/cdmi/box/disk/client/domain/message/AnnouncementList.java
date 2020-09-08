package pw.cdmi.box.disk.client.domain.message;

import java.io.Serializable;
import java.util.List;

/**
 * @version CloudStor CSE Service Platform Subproject, 2015-3-19
 * @see
 * @since
 */
public class AnnouncementList implements Serializable
{
    private static final long serialVersionUID = -1819854091196782648L;

    private Long offset;
    
    private Long startId;
    
    private Integer limit;
    
    private long totalCount;
    
    private List<Announcement> announcements;

    public Long getOffset()
    {
        return offset;
    }

    public void setOffset(Long offset)
    {
        this.offset = offset;
    }

    public Long getStartId()
    {
        return startId;
    }

    public void setStartId(Long startId)
    {
        this.startId = startId;
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

    public List<Announcement> getAnnouncements()
    {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements)
    {
        this.announcements = announcements;
    }
}

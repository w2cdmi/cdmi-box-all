/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.announcement.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.announcement.dao.AnnouncementDao;
import pw.cdmi.box.uam.announcement.domain.Announcement;


@Service("announcementDao")
public class AnnouncementDaoImpl extends AbstractDAOImpl implements AnnouncementDao
{
    private static final String NAME_SPACE = "Announcement.";
    
    @SuppressWarnings("deprecation")
    @Override
    public void save(Announcement announcement)
    {
        sqlMapClientTemplate.insert(NAME_SPACE + "insert", announcement);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Announcement get(long id)
    {
        Object obj = sqlMapClientTemplate.queryForObject(NAME_SPACE + "select", id);
        if (null == obj)
        {
            return null;
        }
        return (Announcement) obj;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void delete(long id)
    {
        sqlMapClientTemplate.delete(NAME_SPACE + "delete", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(Announcement announcement)
    {
        sqlMapClientTemplate.update(NAME_SPACE + "update", announcement);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<Announcement> listAll()
    {
        return sqlMapClientTemplate.queryForList(NAME_SPACE + "listAll");
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<Announcement> listAnnouncement(Limit limit, Long startId)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("limit", limit);
        params.put("startId", startId);
        return sqlMapClientTemplate.queryForList(NAME_SPACE + "listAnnouncement", params);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getTotalCount(long startId)
    {
        Object obj = sqlMapClientTemplate.queryForObject(NAME_SPACE + "getTotalCount", startId);
        if (null == obj)
        {
            return 0;
        }
        return (int) obj;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxId()
    {
        Object obj = sqlMapClientTemplate.queryForObject(NAME_SPACE + "getMaxId");
        if (null == obj)
        {
            return 0;
        }
        return (Long) obj;
    }
    
}

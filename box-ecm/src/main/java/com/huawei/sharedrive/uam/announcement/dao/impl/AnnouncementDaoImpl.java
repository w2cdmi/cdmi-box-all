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
package com.huawei.sharedrive.uam.announcement.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.announcement.dao.AnnouncementDao;
import com.huawei.sharedrive.uam.announcement.domain.Announcement;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@Service("announcementDao")
public class AnnouncementDaoImpl extends AbstractDAOImpl implements AnnouncementDao
{
    private static final String NAME_SPACE = "Announcement.";
    
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

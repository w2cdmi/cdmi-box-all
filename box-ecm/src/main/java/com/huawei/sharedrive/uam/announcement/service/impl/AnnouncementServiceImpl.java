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
package com.huawei.sharedrive.uam.announcement.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.announcement.dao.AnnouncementDao;
import com.huawei.sharedrive.uam.announcement.domain.Announcement;
import com.huawei.sharedrive.uam.announcement.service.AnnouncementService;
import com.huawei.sharedrive.uam.openapi.domain.AnnouncementList;
import com.huawei.sharedrive.uam.openapi.domain.AnnouncementResponse;

import pw.cdmi.box.domain.Limit;

@Service("announcementService")
public class AnnouncementServiceImpl implements AnnouncementService
{
    @Autowired
    private AnnouncementDao announcementDao;
    
    @Override
    public Announcement getAnnouncement(long announcementId)
    {
        return announcementDao.get(announcementId);
    }
    
    @Override
    public AnnouncementList listAnnouncement(long offset, long startId, int length)
    {
        Limit limit = new Limit();
        limit.setLength(length);
        limit.setOffset(offset);
        
        List<Announcement> list = announcementDao.listAnnouncement(limit, startId);
        
        long total = announcementDao.getTotalCount(startId);
        
        AnnouncementList announcementList = new AnnouncementList();
        announcementList.setOffset(offset);
        
        announcementList.setLimit(length);
        announcementList.setTotalCount(total);
        
        if (null != list && !list.isEmpty())
        {
            List<AnnouncementResponse> responseList = new ArrayList<AnnouncementResponse>(list.size());
            AnnouncementResponse response = null;
            for (Announcement announcement : list)
            {
                response = new AnnouncementResponse(announcement);
                responseList.add(response);
            }
            announcementList.setAnnouncements(responseList);
        }
        else
        {
            announcementList.setAnnouncements(new ArrayList<AnnouncementResponse>(0));
        }
        return announcementList;
    }
}

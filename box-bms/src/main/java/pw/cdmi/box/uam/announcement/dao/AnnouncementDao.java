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
package pw.cdmi.box.uam.announcement.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.announcement.domain.Announcement;


public interface AnnouncementDao
{
    void save(Announcement announcement);
    
    Announcement get(long id);
    
    void delete(long id);
    
    void update(Announcement announcement);
    
    List<Announcement> listAll();
    
    List<Announcement> listAnnouncement(Limit limit, Long startId);
    
    int getTotalCount(long startId);
    
    long getMaxId();
}

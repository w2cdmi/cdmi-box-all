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
package com.huawei.sharedrive.uam.announcement.service;

import com.huawei.sharedrive.uam.announcement.domain.Announcement;
import com.huawei.sharedrive.uam.openapi.domain.AnnouncementList;

public interface AnnouncementService
{
    
    /**
     * 获取置顶的系统公告
     * 
     * @param announcementId
     * @return
     */
    Announcement getAnnouncement(long announcementId);
    
    AnnouncementList listAnnouncement(long offset, long startId, int length);
    
}

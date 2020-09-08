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
package pw.cdmi.box.uam.announcement.service;

import java.util.List;

import pw.cdmi.box.uam.announcement.domain.Announcement;
import pw.cdmi.box.uam.announcement.domain.AnnouncementConfig;


public interface AnnouncementService
{
    /**
     * 获取配置
     * 
     * @return
     */
    AnnouncementConfig getAnnouncementConfig();
    
    void saveAnnouncementConfig(AnnouncementConfig config);
    
    /**
     * 创建公告
     * 
     * @param announcement
     * @return
     */
    Announcement createAnnouncement(Announcement announcement);
    
    /**
     * 获取置顶的系统公告
     * 
     * @param announcementId
     * @return
     */
    Announcement getAnnouncement(long announcementId);
    
    /**
     * 获取所有公告，按照是否置顶、以及生成时间倒叙排序
     * 
     * @return
     */
    List<Announcement> listAll();
    
    /**
     * 删除指定的公告
     * 
     * @param announcementId
     * @return
     */
    boolean deleteAnnouncement(long announcementId);
    
    /**
     * 批量删除指定的公告
     * 
     * @param announcementId
     * @return
     */
    boolean deleteAnnouncement(long[] announcementIds);
    
    /**
     * 更新公告
     * 
     * @param announcement
     * @return
     */
    Announcement updateAnnouncement(Announcement announcement);
}

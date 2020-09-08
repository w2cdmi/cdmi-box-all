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
package pw.cdmi.box.uam.announcement.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.announcement.dao.AnnouncementDao;
import pw.cdmi.box.uam.announcement.domain.Announcement;
import pw.cdmi.box.uam.announcement.domain.AnnouncementConfig;
import pw.cdmi.box.uam.announcement.service.AnnouncementIdGenerateService;
import pw.cdmi.box.uam.announcement.service.AnnouncementService;
import pw.cdmi.box.uam.message.domain.Message;
import pw.cdmi.box.uam.message.domain.MessageType;
import pw.cdmi.box.uam.message.service.MessageService;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.DateUtils;


@Service("announcementService")
public class AnnouncementServiceImpl implements AnnouncementService, ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementServiceImpl.class);
    
    @Autowired
    private AnnouncementIdGenerateService announcementIdGenerateService;
    
    @Autowired
    private AnnouncementDao announcementDao;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Value("${message.default.expired.date}")
    private int defaultExpiredDate = 7;
    
    private AnnouncementConfig localCache;
    
    private static final String NAME_ANNOUNCEMENT_ID = "announcementId";
    
    private static final String NAME_ANNOUNCEMENT_TITLE = "title";
    
    private static final String NAME_ANNOUNCEMENT_CONTENT = "content";
    
    @Override
    public AnnouncementConfig getAnnouncementConfig()
    {
        if (null == localCache)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(Constants.UAM_DEFAULT_APP_ID,
                null,
                AnnouncementConfig.ANNOUNCEMENT_CONFIG_PREFIX);
            AnnouncementConfig config = AnnouncementConfig.buildAccessAddressConfig(itemList);
            
            // 如果为保存，需要返回默认值
            if (null == config || config.getMessageSavingTimes() <= 0)
            {
                config = new AnnouncementConfig();
                config.setMessageSavingTimes(defaultExpiredDate);
            }
            
            localCache = config;
        }
        return localCache;
    }
    
    @Override
    public void saveAnnouncementConfig(AnnouncementConfig config)
    {
        saveInNewTransaction(config);
        configManager.setConfig(AnnouncementConfig.class.getSimpleName(), config);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInNewTransaction(AnnouncementConfig config)
    {
        config.setAppId(Constants.UAM_DEFAULT_APP_ID);
        List<SystemConfig> itemList = config.toConfigItem();
        
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.getByPriKey(Constants.UAM_DEFAULT_APP_ID, systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        localCache = config;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Announcement createAnnouncement(Announcement announcement)
    {
        announcement.setId(announcementIdGenerateService.getNextId());
        
        Date createdAt = new Date();
        announcement.setPublishTime(createdAt);
        
        announcementDao.save(announcement);
        
        // 生成message信息
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put(NAME_ANNOUNCEMENT_ID, announcement.getId());
        params.put(NAME_ANNOUNCEMENT_TITLE, announcement.getTitle());
        params.put(NAME_ANNOUNCEMENT_CONTENT, announcement.getContent());
        
        Message message = new Message();
        message.setProviderId(announcement.getPublisherId());
        message.setType(MessageType.SYSTEM_ANNOUNCEMENT);
        message.setCreatedAt(createdAt);
        
        AnnouncementConfig config = this.getAnnouncementConfig();
        Date expiredAt = DateUtils.getDateAfter(createdAt, config.getMessageSavingTimes());
        
        message.setExpiredAt(expiredAt);
        message.setParams(params);
        
        messageService.create(message);
        
        return announcement;
    }
    
    @Override
    public Announcement getAnnouncement(long announcementId)
    {
        return announcementDao.get(announcementId);
    }
    
    @Override
    public List<Announcement> listAll()
    {
        return announcementDao.listAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteAnnouncement(long announcementId)
    {
        announcementDao.delete(announcementId);
        return true;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteAnnouncement(long[] announcementIds)
    {
        for (long id : announcementIds)
        {
            announcementDao.delete(id);
        }
        return false;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Announcement updateAnnouncement(Announcement announcement)
    {
        announcementDao.update(announcement);
        
        return announcement;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(AnnouncementConfig.class.getSimpleName()))
        {
            LOGGER.info("Change AnnouncementConfig By Cluseter Notify.");
            localCache = (AnnouncementConfig) value;
        }
    }
}

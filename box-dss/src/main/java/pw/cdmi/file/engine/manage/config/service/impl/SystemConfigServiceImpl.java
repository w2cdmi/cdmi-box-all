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
package pw.cdmi.file.engine.manage.config.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.dao.SystemConfigDao;
import pw.cdmi.file.engine.manage.config.service.SystemConfigService;

/**
 * 
 * @author s90006125
 * 
 */
@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigServiceImpl.class);
    
    @Autowired
    private SystemConfigDao systemConfigDao;
    
    @Override
    @MethodLogAble(Level.INFO)
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSystemConfig(SystemConfig config)
    {
        changeConfig(config);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSystemConfigs(SystemConfig... configs)
    {
        for (SystemConfig config : configs)
        {
            changeConfig(config);
        }
    }
    
    private void changeConfig(SystemConfig config)
    {
        SystemConfig original = SystemConfigContainer.getConfig(config.getKey());
        
        if (null == original)
        {
            LOGGER.warn("original is null");
            return;
        }
        
        if (StringUtils.isBlank(config.getDescription()))
        {
            config.setDescription(original.getDescription());
        }
        
        config.setChangeAble(original.isChangeAble());
        config.setShowAble(original.isShowAble());
        
        LOGGER.info("Change [ " + original + " ] To [ " + config + " ]");
        systemConfigDao.update(config);
        
        SystemConfigContainer.regiestConfig(config);
    }
}

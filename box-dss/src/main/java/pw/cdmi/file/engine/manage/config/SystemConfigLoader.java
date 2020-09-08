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
package pw.cdmi.file.engine.manage.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.dao.SystemConfigDao;

/**
 * 系统属性加载器
 * 
 * @author s90006125
 * 
 */
public class SystemConfigLoader
{
    @Autowired
    private SystemConfigDao systemConfigDao;
    
    public void load()
    {
        List<SystemConfig> configs = systemConfigDao.selectAll();
        
        SystemConfigContainer.regiestConfig(configs);
    }
}

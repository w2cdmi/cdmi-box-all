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
package pw.cdmi.file.engine.manage.config.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.manage.config.dao.SystemConfigDao;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings({"unused", "deprecation", "unchecked"})
@Repository("systemConfigDao")
public class SystemConfigDaoImpl extends IbatisSupportDAO<SystemConfig> implements SystemConfigDao
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigDaoImpl.class);
    
    @Override
    public List<SystemConfig> selectAll()
    {
        return sqlMapClientTemplate.queryForList(warpSqlstatement(SystemConfig.class, SQL_SELECT_ALL));
    }
    
    @Override
    protected void doInsert(SystemConfig obj)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected void doDelete(SystemConfig obj)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected SystemConfig doSelect(SystemConfig obj)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected int doUpdate(SystemConfig obj)
    {
        return sqlMapClientTemplate.update(warpSqlstatement(SystemConfig.class, SQL_UPDATE), obj);
    }
}

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
package com.huawei.sharedrive.app.dataserver.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.dao.DCDao;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings("deprecation")
@Service("dcDao")
public class DCDaoImpl extends AbstractDAOImpl implements DCDao
{
    @Override
    public void create(DataCenter t)
    {
        sqlMapClientTemplate.insert("DataCenter.insert", t);
    }
    
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.delete("DataCenter.delete", id);
    }
    
    @Override
    public DataCenter get(Integer id)
    {
        return (DataCenter) sqlMapClientTemplate.queryForObject("DataCenter.select", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<DataCenter> getAll()
    {
        return sqlMapClientTemplate.queryForList("DataCenter.selectAll");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<DataCenter> getAllPriorityDataCenter()
    {
        return sqlMapClientTemplate.queryForList("DataCenter.selectAllPriority");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<DataCenter> getAllByRegion(int regionID)
    {
        return sqlMapClientTemplate.queryForList("DataCenter.selectAllByRegion", regionID);
    }
    
    @Override
    public DataCenter getByName(String name)
    {
        return (DataCenter) sqlMapClientTemplate.queryForObject("DataCenter.selectByName", name);
    }
    
    @Override
    public int getNextId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "dcId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return (int) id;
    }
    
    @Override
    public void update(DataCenter t)
    {
        sqlMapClientTemplate.update("DataCenter.update", t);
    }
}

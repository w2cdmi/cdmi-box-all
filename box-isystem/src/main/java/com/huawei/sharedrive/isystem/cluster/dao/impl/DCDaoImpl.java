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
package com.huawei.sharedrive.isystem.cluster.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

/**
 * 
 * @author s90006125
 * 
 */
@Service("dcDao")
public class DCDaoImpl extends AbstractDAOImpl implements DCDao
{
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<DataCenter> getAll()
    {
        return sqlMapClientTemplate.queryForList("DataCenter.selectAll");
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<DataCenter> getAllByRegion(int regionID)
    {
        return sqlMapClientTemplate.queryForList("DataCenter.selectAllByRegion", regionID);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public DataCenter get(Integer id)
    {
        return (DataCenter) sqlMapClientTemplate.queryForObject("DataCenter.select", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public DataCenter getByID(Integer id)
    {
        return (DataCenter) sqlMapClientTemplate.queryForObject("DataCenter.selectByID", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public DataCenter getByName(String name)
    {
        return (DataCenter) sqlMapClientTemplate.queryForObject("DataCenter.selectByName", name);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(DataCenter t)
    {
        sqlMapClientTemplate.insert("DataCenter.insert", t);
    }
    
    @Override
    public void update(DataCenter t)
    {
        sqlMapClientTemplate.update("DataCenter.update", t);
    }
    
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.delete("DataCenter.delete", id);
    }
    
    @Override
    public int getNextId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "dcId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setAllPriorityDefault(int regionid)
    {
        //将该区域下的所有数据中心优先级设置为0
        sqlMapClientTemplate.update("DataCenter.setAllPriorityDefault", regionid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setPriority(int regionid, int id)
    {
        // 在指定区域下的指定数据中心优先级设置为1
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("regionid", regionid);
        map.put("id", id);
        sqlMapClientTemplate.update("DataCenter.setPriority", map);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setPriorityDefault(int regionid, int id)
    {
        // 在指定区域下的指定数据中心优先级设置为0
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("regionid", regionid);
        map.put("id", id);
        sqlMapClientTemplate.update("DataCenter.setPriorityDefault", map);
    }
}

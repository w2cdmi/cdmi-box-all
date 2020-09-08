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

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.dataserver.dao.NetConfigDao;
import com.huawei.sharedrive.app.dataserver.domain.NetSegment;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

/**
 * 
 * @author s90006125
 *
 */
@SuppressWarnings("deprecation")
@Repository("netConfigDao")
public class NetConfigDaoImpl extends AbstractDAOImpl implements NetConfigDao
{
    @Override
    public void create(NetSegment t)
    {
        sqlMapClientTemplate.insert("NetSegment.insert", t);
    }

    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("NetSegment.delete", id);
    }

    @Override
    public NetSegment get(Long id)
    {
        return (NetSegment) sqlMapClientTemplate.queryForObject("NetSegment.select", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NetSegment> getAll()
    {
        return sqlMapClientTemplate.queryForList("NetSegment.selectAll");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NetSegment> getAllByRegion(int regionID)
    {
        return sqlMapClientTemplate.queryForList("NetSegment.selectAllByRegion", regionID);
    }
    
    @Override
    public int getNextId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "netSegment");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return (int) id;
    }
    
    @Override
    public void update(NetSegment t)
    {
        sqlMapClientTemplate.update("NetSegment.update", t);
    }
}

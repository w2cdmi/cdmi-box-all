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
package pw.cdmi.file.engine.manage.datacenter.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.manage.datacenter.dao.DCDao;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;

/**
 * 
 * @author s90006125
 * 
 */
@Repository("dcDao")
@SuppressWarnings("deprecation")
public class DCDaoImpl extends IbatisSupportDAO<ClusterNode> implements DCDao
{
    @Override
    protected void doInsert(ClusterNode obj)
    {
        sqlMapClientTemplate.insert(warpSqlstatement(ClusterNode.class, SQL_INSERT), obj);
    }
    
    @Override
    protected void doDelete(ClusterNode obj)
    {
        sqlMapClientTemplate.delete(warpSqlstatement(ClusterNode.class, SQL_DELETE), obj);
    }
    
    @Override
    protected ClusterNode doSelect(ClusterNode obj)
    {
        return (ClusterNode) sqlMapClientTemplate.queryForObject(warpSqlstatement(ClusterNode.class,
            SQL_SELECT),
            obj);
    }
    
    @Override
    protected int doUpdate(ClusterNode obj)
    {
        return sqlMapClientTemplate.update(warpSqlstatement(ClusterNode.class, SQL_UPDATE), obj);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ClusterNode> selectAll()
    {
        return sqlMapClientTemplate.queryForList(warpSqlstatement(ClusterNode.class, SQL_SELECT_ALL));
    }
    
}

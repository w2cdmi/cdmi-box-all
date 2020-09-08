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
package pw.cdmi.file.engine.manage.datacenter.dao;

import java.util.List;

import pw.cdmi.core.db.dao.BaseDAO;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;

/**
 * 
 * @author s90006125
 * 
 */
public interface DCDao extends BaseDAO<ClusterNode>
{
    /**
     * 查询出所有
     * 
     * @return
     */
    List<ClusterNode> selectAll();
}

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
package com.huawei.sharedrive.isystem.cluster.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;

import pw.cdmi.box.dao.BaseDAO;

/**
 * 
 * @author s90006125
 * 
 */
public interface DCDao extends BaseDAO<DataCenter, Integer>
{
    DataCenter getByName(String name);
    
    List<DataCenter> getAll();
    
    List<DataCenter> getAllByRegion(int regionID);
    
    int getNextId();

    DataCenter getByID(Integer id);

    void setAllPriorityDefault(int regionid);

    void setPriority(int regionid, int dcid);

    void setPriorityDefault(int regionid, int dcid);
}

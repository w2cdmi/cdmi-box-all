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

import com.huawei.sharedrive.isystem.cluster.domain.NetSegment;

import pw.cdmi.box.dao.BaseDAO;

/**
 * 
 * @author s90006125
 *
 */
public interface NetConfigDao extends BaseDAO<NetSegment, Long>
{
    List<NetSegment> getAll();
    
    List<NetSegment> getAllByRegion(int regionID);
    
    int getNextId();
}

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
package com.huawei.sharedrive.app.dataserver.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.dao.NetConfigDao;
import com.huawei.sharedrive.app.dataserver.domain.NetSegment;
import com.huawei.sharedrive.app.dataserver.service.NetConfigService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.IpUtils;

/**
 * 
 * @author s90006125
 * 
 */
@Component("netConfigService")
public class NetConfigServiceImpl implements NetConfigService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NetConfigServiceImpl.class);
    
    @Autowired
    private NetConfigDao netConfigDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addNetSegment(List<NetSegment> netSegments)
    {
        if (null == netSegments || netSegments.isEmpty())
        {
            return;
        }
        for (NetSegment netSegment : netSegments)
        {
            if (!netSegment.validate())
            {
                String message = "NetSegment [ Start: " + netSegment.getStartIp() + "; End: "
                    + netSegment.getEndIp() + " ] Is Illegal.";
                LOGGER.warn(message);
                throw new InnerException(message);
            }
            
            netSegment.setId(netConfigDao.getNextId());
            netConfigDao.create(netSegment);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeNetSegment(List<NetSegment> netSegments)
    {
        if (null == netSegments || netSegments.isEmpty())
        {
            return;
        }
        for (NetSegment netSegment : netSegments)
        {
            if (!netSegment.validate())
            {
                String message = "NetSegment [ Start: " + netSegment.getStartIp() + "; End: "
                    + netSegment.getEndIp() + " ] Is Illegal.";
                LOGGER.warn(message);
                throw new InnerException(message);
            }
            netConfigDao.update(netSegment);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNetSegment(List<NetSegment> netSegments)
    {
        if (null == netSegments || netSegments.isEmpty())
        {
            return;
        }
        for (NetSegment netSegment : netSegments)
        {
            netConfigDao.delete(netSegment.getId());
        }
    }
    
    @Override
    public List<NetSegment> getAllNetSegment()
    {
        return netConfigDao.getAll();
    }
    
    @Override
    public List<NetSegment> getAllNetSegmentByRegion(int regionID)
    {
        return netConfigDao.getAllByRegion(regionID);
    }
    
    @Override
    public NetSegment findNetSegment(String strIp)
    {
        List<NetSegment> list = getAllNetSegment();
        if (null == list || list.isEmpty())
        {
            return null;
        }
        
        long ip = IpUtils.toLong(strIp);
        if (ip < 0)
        {
            return null;
        }
        for (NetSegment netSegment : list)
        {
            if (ip >= netSegment.getStart() && ip <= netSegment.getEnd())
            {
                return netSegment;
            }
        }
        
        return null;
    }
    
}

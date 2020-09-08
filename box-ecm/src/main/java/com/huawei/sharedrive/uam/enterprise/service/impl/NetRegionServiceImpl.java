package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.AccessSpaceConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.NetRegionDao;
import com.huawei.sharedrive.uam.enterprise.dao.NetRegionIpDao;
import com.huawei.sharedrive.uam.enterprise.dao.ResourceStrategyDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class NetRegionServiceImpl implements NetRegionService
{
    
    @Autowired
    private NetRegionDao netRegionDao;
    
    @Autowired
    private AccessConfigDao accessConfigDao;
    
    @Autowired
    private NetRegionIpDao netRegionIpDao;
    
    @Autowired
    private ResourceStrategyDao resourceStrategyDao;
    
    @Autowired
    private AccessSpaceConfigDao accessSpaceConfigDao;
    
    @Override
    public long create(NetRegion netRegion)
    {
        Date now = new Date();
        netRegion.setCreatedAt(now);
        netRegion.setModifiedAt(now);
        return netRegionDao.create(netRegion);
        
    }
    
    @Override
    public boolean isDuplicateValues(NetRegion netRegion)
    {
        return netRegionDao.isDuplicateValues(netRegion);
    }
    
    @Override
    public List<NetRegion> getFilterdList(NetRegion filter)
    {
        
        List<NetRegion> content = netRegionDao.getFilterd(filter, null, null);
        return content;
        
    }
    
    @Override
    public List<NetRegionIp> getFilterdList(NetRegionIp filter)
    {
        
        List<NetRegionIp> content = netRegionIpDao.getFilterd(filter, null, null);
        return content;
        
    }
    
    @Override
    public Page<NetRegion> getFilterd(NetRegion filter, PageRequest pageRequest)
    {
        
        int total = netRegionDao.getFilterdCount(filter);
        List<NetRegion> content = netRegionDao.getFilterd(filter,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        
        for (NetRegion sr : content)
        {
            sr.setNetRegionName(HtmlUtils.htmlEscape(sr.getNetRegionName()));
            sr.setNetRegionDesc(HtmlUtils.htmlEscape(sr.getNetRegionDesc()));
        }
        Page<NetRegion> page = new PageImpl<NetRegion>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public Page<NetRegionIp> getFilterd(NetRegionIp filter, PageRequest pageRequest)
    {
        
        int total = netRegionIpDao.getFilterdCount(filter);
        List<NetRegionIp> content = netRegionIpDao.getFilterd(filter,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<NetRegionIp> page = new PageImpl<NetRegionIp>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public NetRegion getById(long id)
    {
        
        return netRegionDao.getById(id);
    }
    
    @Override
    public void updateSecurityRole(NetRegion netRegion)
    {
        Date now = new Date();
        netRegion.setModifiedAt(now);
        netRegionDao.updateEnterpriseInfo(netRegion);
        
    }
    
    @Override
    public void updateSecurityRole(NetRegionIp netRegion)
    {
        Date now = new Date();
        netRegion.setModifiedAt(now);
        netRegionIpDao.updateEnterpriseInfo(netRegion);
        
    }
    
    @Override
    public void updateSecurityRole(NetRegionIp netRegion, long id)
    {
        Date now = new Date();
        netRegion.setModifiedAt(now);
        netRegion.setNetRegionId(id);
        netRegionIpDao.updateEnterpriseInfo(netRegion);
        
    }
    
    @Override
    public long getByDomainExclusiveId(NetRegion netRegion)
    {
        return netRegionDao.getByDomainExclusiveId(netRegion);
    }
    
    @Override
    public boolean isDuplicateNetConfigValues(NetRegionIp netRegion)
    {
        return netRegionIpDao.isDuplicateValues(netRegion);
    }
    
    @Override
    public long create(NetRegionIp netRegion)
    {
        Date now = new Date();
        netRegion.setCreatedAt(now);
        netRegion.setModifiedAt(now);
        return netRegionIpDao.create(netRegion);
        
    }
    
    @Override
    public void delete(long id)
    {
        netRegionIpDao.delete(id);
    }
    
    @Override
    public List<NetRegionIp> getNetRegionList(NetRegionIp netRegionIp)
    {
        List<NetRegionIp> content = netRegionIpDao.getListRegionIp(netRegionIp);
        return content;
    }
    
    @Override
    public NetRegionIp getByIp(long accountId, long ipLong)
    {
        return netRegionIpDao.getByIp(accountId, ipLong);
    }
    
    @Override
    public void deleteNetRegion(NetRegion netRegion)
    {
        long accountId = netRegion.getAccountId();
        long netRegionId = netRegion.getId();
        
        AccessConfig accessConfig = new AccessConfig();
        accessConfig.setAccountId(accountId);
        accessConfig.setNetRegionId(netRegionId);
        int accessConfigCount = accessConfigDao.getFilterdCount(accessConfig);
        if (accessConfigCount > 0)
        {
            throw new InvalidParamterException();
        }
        
        ResourceStrategy resourceStrategy = new ResourceStrategy();
        resourceStrategy.setAccountId(accountId);
        resourceStrategy.setNetRegionId(netRegionId);
        int resourceStrategyCount = resourceStrategyDao.getFilterdCount(resourceStrategy);
        if (resourceStrategyCount > 0)
        {
            throw new InvalidParamterException();
        }
        
        AccessSpaceConfig accessSpaceConfig = new AccessSpaceConfig();
        accessSpaceConfig.setAccountId(accountId);
        accessSpaceConfig.setNetRegionId(netRegionId);
        int accessSpaceConfigCount = accessSpaceConfigDao.getFilterdCount(accessSpaceConfig);
        if (accessSpaceConfigCount > 0)
        {
            throw new InvalidParamterException();
        }
        
        netRegionDao.deleteNetRegion(netRegion);
        
        NetRegionIp netRegionIp = new NetRegionIp();
        netRegionIp.setAccountId(accountId);
        netRegionIp.setNetRegionId(netRegionId);
        netRegionIpDao.deleteByCondition(netRegionIp);
    }
    
    @Override
    public NetRegionIp getNetRegionIpById(long id)
    {
        return netRegionIpDao.getById(id);
    }
    
}

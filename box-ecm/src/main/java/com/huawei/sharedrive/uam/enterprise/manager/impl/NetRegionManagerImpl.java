package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.exception.ExistEnterpriseConflictException;
import com.huawei.sharedrive.uam.exception.ExistNetworkRegionIpConflictException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.IpUtils;

@Component
public class NetRegionManagerImpl implements NetRegionManager
{
    
    @Autowired
    private NetRegionService netRegionService;
    
    @Override
    public long create(NetRegion netRegion) throws IOException
    {
        
        if (netRegionService.isDuplicateValues(netRegion))
        {
            throw new ExistEnterpriseConflictException();
        }
        long id = netRegionService.create(netRegion);
        
        return id;
        
    }
    
    @Override
    public Page<NetRegion> getFilterd(NetRegion netRegion, PageRequest pageRequest)
    {
        return netRegionService.getFilterd(netRegion, pageRequest);
    }
    
    @Override
    public Page<NetRegionIp> getFilterd(NetRegionIp netRegionIp, PageRequest pageRequest)
    {
        return netRegionService.getFilterd(netRegionIp, pageRequest);
    }
    
    @Override
    public NetRegion getById(long id)
    {
        return netRegionService.getById(id);
    }
    
    @Override
    public NetRegionIp getByNetRegionIpId(long id)
    {
        return netRegionService.getNetRegionIpById(id);
    }
    
    @Override
    public void updateEnterpriseInfo(NetRegion netRegion)
    {
        
        netRegionService.updateSecurityRole(netRegion);
        
    }
    
    @Override
    public long getByDomainExclusiveId(NetRegion netRegion)
    {
        
        return netRegionService.getByDomainExclusiveId(netRegion);
    }
    
    @Override
    public void modify(NetRegion netRegion) throws IOException
    {
        netRegionService.updateSecurityRole(netRegion);
    }
    
    @Override
    public void modify(NetRegionIp netRegionIp) throws IOException
    {
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpStart()))
        {
            throw new InvalidParamterException();
        }
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpEnd()))
        {
            throw new InvalidParamterException();
        }
        netRegionService.updateSecurityRole(netRegionIp);
    }
    
    @Override
    public void modify(NetRegionIp netRegionIp, long id) throws IOException
    {
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpStart()))
        {
            throw new InvalidParamterException();
        }
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpEnd()))
        {
            throw new InvalidParamterException();
        }
        netRegionService.updateSecurityRole(netRegionIp, id);
    }
    
    @Override
    public long create(NetRegionIp netRegionIp) throws IOException
    {
        if (netRegionService.isDuplicateNetConfigValues(netRegionIp))
        {
            throw new ExistNetworkRegionIpConflictException();
        }
        
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpStart()))
        {
            throw new InvalidParamterException("invalid ip address");
        }
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpEnd()))
        {
            throw new InvalidParamterException("invalid ip address");
        }
        long ipStart = IpUtils.toLong(netRegionIp.getIpStart());
        long ipEnd = IpUtils.toLong(netRegionIp.getIpEnd());
        if (ipStart > ipEnd)
        {
            throw new InvalidParamterException();
        }
        
        // judge ip if exists in the current network config;
        boolean isAllowed = judgeIpExistsNetwork(netRegionIp, ipStart);
        if (!isAllowed)
        {
            throw new ExistNetworkRegionIpConflictException();
        }
        isAllowed = judgeIpExistsNetwork(netRegionIp, ipEnd);
        if (!isAllowed)
        {
            throw new ExistNetworkRegionIpConflictException();
        }
        netRegionIp.setIpStartValue(ipStart);
        netRegionIp.setIpEndValue(ipEnd);
        
        return netRegionService.create(netRegionIp);
        
    }
    
    @Override
    public long create(NetRegionIp netRegionIp, NetRegion netRegion, HttpServletRequest req)
        throws IOException
    {
        if (netRegionService.isDuplicateNetConfigValues(netRegionIp))
        {
            delete(netRegion);
            throw new ExistNetworkRegionIpConflictException();
        }
        
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpStart()))
        {
            delete(netRegion);
            throw new InvalidParamterException("invalid ip address");
        }
        if (!IpUtils.isIPv4LiteralAddress(netRegionIp.getIpEnd()))
        {
            delete(netRegion);
            throw new InvalidParamterException("invalid ip address");
        }
        long ipStart = IpUtils.toLong(netRegionIp.getIpStart());
        long ipEnd = IpUtils.toLong(netRegionIp.getIpEnd());
        if (ipStart > ipEnd)
        {
            delete(netRegion);
            throw new InvalidParamterException();
        }
        
        // judge ip if exists in the current network config;
        boolean isAllowed = judgeIpExistsNetwork(netRegionIp, ipStart);
        if (!isAllowed)
        {
            delete(netRegion);
            throw new ExistNetworkRegionIpConflictException();
        }
        isAllowed = judgeIpExistsNetwork(netRegionIp, ipEnd);
        if (!isAllowed)
        {
            delete(netRegion);
            throw new ExistNetworkRegionIpConflictException();
        }
        netRegionIp.setIpStartValue(ipStart);
        netRegionIp.setIpEndValue(ipEnd);
        
        return netRegionService.create(netRegionIp);
        
    }
    
    private boolean judgeIpExistsNetwork(NetRegionIp netRegionIp, long ipStart)
    {
        NetRegionIp region = netRegionService.getByIp(netRegionIp.getAccountId(), ipStart);
        if (null == region)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public List<NetRegion> getFilterdNetRegionList(NetRegion netRegion)
    {
        return netRegionService.getFilterdList(netRegion);
    }
    
    @Override
    public List<NetRegionIp> getFilterdList(NetRegionIp netRegionIp)
    {
        return netRegionService.getFilterdList(netRegionIp);
    }
    
    public void delete(int id)
    {
        netRegionService.delete(id);
    }
    
    @Override
    public List<NetRegionIp> getListRegionIp(NetRegionIp netRegionIp)
    {
        return netRegionService.getNetRegionList(netRegionIp);
    }
    
    @Override
    public void delete(NetRegion netRegion) throws IOException
    {
        netRegionService.deleteNetRegion(netRegion);
        
    }
}

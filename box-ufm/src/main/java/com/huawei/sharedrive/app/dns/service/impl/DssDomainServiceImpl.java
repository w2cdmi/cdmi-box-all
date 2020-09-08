package com.huawei.sharedrive.app.dns.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dns.dao.DssDomainDao;
import com.huawei.sharedrive.app.dns.domain.DssDomain;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.dns.service.InnerLoadBalanceManager;
import com.huawei.sharedrive.app.system.dns.DssDnsListener;


@Service("dssDomainService")
public class DssDomainServiceImpl implements DssDomainService
{
    @Autowired
    private DssDomainDao dssDomainDao;
   
    @Autowired
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    @Override
    public List<DssDomain> getAll()
    {
        return dssDomainDao.getAll();
    }
    
    @Override
    public List<DssDomain> getByDssId(int dssId)
    {
        return dssDomainDao.getByDssId(dssId);
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DssDomainServiceImpl.class);
    
    @Override
    public String getOuterDomainByDssId(ResourceGroup group)
    {
        try
        {
            DssDomain domain = DssDnsListener.getDssDomain(group.getId());
            if(domain == null)
            {
                return getServiceAddrFromDB(group);
            }
            return domain.getDomain();
        }
        catch(Exception e)
        {
            LOGGER.error("[dssDnsLog]can not get domain from dssDnsListener");
            return getServiceAddrFromDB(group);
        }
    }
    
    @Override
    public String getDomainByDssId(ResourceGroup group)
    {
        if (isMerge(group.getType()))
        {
            //使用本地负载均衡
            if(innerLoadBalanceManager.isSysInnerLoadblanceConfig())
            {
                String serverIp= innerLoadBalanceManager.getBestDSSServiceByGroupId(group.getId());
                if(StringUtils.isNotBlank(serverIp))
                {
                    return serverIp;
                }
            }
        }
        else
        {
          //使用LVS ip， ip是DC对接填入的虚 ip
            LOGGER.info("LVS loadBalance,get ip:" + group.getManageIp());
            return group.getManageIp();
        }
        
        DssDomain domain = DssDnsListener.getDssDomain(group.getId());
        if(domain == null)
        {
            return getServiceAddrFromDB(group);
        }
        return domain.getDomain();
    }

    private String getServiceAddrFromDB(ResourceGroup group)
    {
        List<DssDomain> list = dssDomainDao.getByDssId(group.getId());
        if (!list.isEmpty())
        {
            return list.get(0).getDomain();
        }
        //使用LVS ip， ip是DC对接填入的虚 ip
        return group.getManageIp();
    }
    
    private boolean isMerge(int type)
    {
        return type == ResourceGroup.Type.Merge.getCode();
    }
   
}

package com.huawei.sharedrive.app.system.dns;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dns.dao.DssDomainDao;
import com.huawei.sharedrive.app.dns.domain.DssDomain;

import pw.cdmi.common.config.service.ConfigListener;

@Component("dssDnsListener")
public class DssDnsListener implements ConfigListener
{
    public static final String DSS_DNS_CHANGE_KEY = "dssDnsChanged";
    
    private static List<DssDomain> dssDomainList = new ArrayList<DssDomain>(10);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DssDnsListener.class);
    
    public static DssDomain getDssDomain(int dssId)
    {
        for(DssDomain dssDomain: dssDomainList)
        {
            if(dssDomain.getDssId() == dssId)
            {
                return dssDomain;
            }
        }
        LOGGER.error("Can not find the dssDomain for " + dssId);
        return null;
    }
    
    @Autowired
    private DssDomainDao dssDomainDao;
    
    @Override
    public void configChanged(String key, Object valueObj)
    {
        if(!StringUtils.equals(DSS_DNS_CHANGE_KEY, key))
        {
            return;
        }
        setDssDomainList(dssDomainDao.getAll());
    }
    
    @PostConstruct
    public void init()
    {
        setDssDomainList(dssDomainDao.getAll());
    }
    
    private static void setDssDomainList(List<DssDomain> list)
    {
        dssDomainList = list;
    }
    
}

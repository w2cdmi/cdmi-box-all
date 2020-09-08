package com.huawei.sharedrive.uam.system.dao;

import java.util.List;

import com.huawei.sharedrive.uam.system.domain.MailServer;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface MailServerDao
{
    
    MailServer get(Long id);
    
    List<MailServer> getFilterd(MailServer filter, Order order, Limit limit);
    
    int getFilterdCount(MailServer filter);
    
    void delete(Long id);
    
    MailServer getDefaultMailServer();
    
    long getNextAvailableId();
    
    MailServer getByAppId(String appId);
    
    MailServer getByAccountId(long accountId);
    
}

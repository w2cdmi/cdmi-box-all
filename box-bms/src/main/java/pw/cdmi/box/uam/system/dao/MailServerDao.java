package pw.cdmi.box.uam.system.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.system.domain.MailServer;

public interface MailServerDao
{
    
    MailServer get(Long id);
    
    List<MailServer> getFilterd(MailServer filter, Order order, Limit limit);
    
    int getFilterdCount(MailServer filter);
    
    void delete(Long id);
    
    void create(MailServer mailServer);
    
    void updateMailServer(MailServer mailServer);
    
    MailServer getDefaultMailServer();
    
    long getNextAvailableId();
    
    MailServer getByAppId(String appId);
    
}

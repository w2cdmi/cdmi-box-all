package pw.cdmi.box.disk.authapp.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppDao
{
    
    AuthApp getByAuthAppName(String name);
    
    AuthApp getByAuthAppID(String authAppId);
    
    List<AuthApp> getFilterd(AuthApp filter, Order order, Limit limit);
    
    int getFilterdCount(AuthApp filter);
    
    AuthApp getDefaultWebApp();
    
}

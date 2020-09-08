package pw.cdmi.box.uam.authapp.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppDao
{
    
    AuthApp getByAuthAppID(String authAppId);
    
    List<AuthApp> getFilterd(AuthAppExtend filter, Order order, Limit limit);
    
    int getFilterdCount(AuthAppExtend filter);
    
    void delete(String authAppId);
    
    void create(AuthApp authApp);
    
    void updateAuthApp(AuthApp authApp);
    
    AuthApp getDefaultWebApp();
    
    int getCountByAuthentication(long enterpriseId);
    
    List<AuthApp> getByAuthentication(long enterpriseId, Limit limit);
    
    void updateNetworkRegionStatus(AuthApp authApp);
    
    int getAuthAppNum();
}

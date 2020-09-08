package pw.cdmi.box.uam.authapp.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.box.uam.exception.AuthFailedException;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppService
{
    AuthApp getByAuthAppID(String authAppId);
    
    AuthApp getDefaultWebApp();
    
    List<AuthApp> getAuthAppList(AuthAppExtend filter, Order order, Limit limit);
    
    void delete(String authAppId);
    
    void create(AuthApp authApp);
    
    void updateAuthApp(AuthApp authApp);
    
    String checkAuthApp(String authorization, String date) throws AuthFailedException;
    
    int getCountByAuthentication(long enterpriseId);
    
    List<AuthApp> getByAuthentication(long enterpriseId, Limit limit);
    
    void updateNetworkRegionStatus(AuthApp authApp);
    
    int getAuthAppNum();
}

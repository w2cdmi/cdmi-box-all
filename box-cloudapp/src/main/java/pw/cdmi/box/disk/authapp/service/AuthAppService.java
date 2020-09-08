/**
 * 
 */
package pw.cdmi.box.disk.authapp.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppService
{
    AuthApp getByAuthAppID(String authAppId);
    
    AuthApp getByAuthAppName(String name);
    
    List<AuthApp> getAuthAppList(AuthApp filter, Order order, Limit limit);
    
    String getCurrentAppId();
    
}

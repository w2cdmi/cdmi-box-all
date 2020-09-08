package pw.cdmi.box.uam.authapp.manager;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.domain.AuthAppExtend;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppManager
{
    
    List<AuthApp> getAuthAppList(AuthAppExtend filter, Order order, Limit limit);
    
    Page<AuthApp> getByAuthentication(long enterpriseId, PageRequest pageRequest);
    
    AuthApp getByAuthAppID(String authAppId);
    
}

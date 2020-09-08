/**
 * 
 */
package com.huawei.sharedrive.app.authapp.service;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.AuthFailedException;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
public interface AuthAppService
{
    AuthApp getByAuthAppID(String authAppId);
    
    List<AuthApp> getAuthAppList(AuthApp filter, OrderV1 order, Limit limit);
    
    void checkAuthApp(String authorization, String date) throws AuthFailedException;
    
}

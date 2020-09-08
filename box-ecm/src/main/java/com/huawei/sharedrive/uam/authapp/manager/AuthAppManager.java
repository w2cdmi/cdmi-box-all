package com.huawei.sharedrive.uam.authapp.manager;

import java.util.List;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppManager
{
    
    Page<AuthApp> getByAuthentication(long enterpriseId, PageRequest pageRequest);
    
    AuthApp getByAuthAppID(String authAppId);
    
    List<String> getAppId();
    
}

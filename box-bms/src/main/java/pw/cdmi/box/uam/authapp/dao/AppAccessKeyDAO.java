package pw.cdmi.box.uam.authapp.dao;

import java.util.List;

import pw.cdmi.common.domain.AppAccessKey;

public interface AppAccessKeyDAO
{
    
    AppAccessKey getById(String id);
    
    List<AppAccessKey> getByAppId(String appId);
    
    int delete(String id);
    
    int deleteByAppId(String appId);
    
    void create(AppAccessKey appAccessKey);
    
}

/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.AppManageDAO;
import com.huawei.sharedrive.app.system.domain.RegistApp;
import com.huawei.sharedrive.app.system.service.AppManageService;
import com.huawei.sharedrive.app.utils.RandomKeyGUID;

/**
 * @author s00108907
 * 
 */
@Component
public class AppManageServiceImpl implements AppManageService
{
    
    @Autowired
    private AppManageDAO appManageDAO;
    
    @Override
    public void create(RegistApp registApp)
    {
        String key = RandomKeyGUID.getSecureRandomGUID();
        registApp.setId(RandomKeyGUID.getSecureRandomGUID());
        registApp.setSecretKey(key);
        Date now = new Date();
        registApp.setCreateAt(now);
        registApp.setModifyAt(now);
        appManageDAO.create(registApp);
    }
    
    @Override
    public void delete(String id)
    {
        appManageDAO.delete(id);
    }
    
    @Override
    public RegistApp get(String id)
    {
        return appManageDAO.get(id);
    }
    
    @Override
    public List<RegistApp> getAll()
    {
        return appManageDAO.getAll();
    }
    
    @Override
    public void sendMail(RegistApp registApp, String reciver)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void updateKey(String id)
    {
        RegistApp registApp = new RegistApp();
        registApp.setId(id);
        String key = RandomKeyGUID.getSecureRandomGUID();
        registApp.setSecretKey(key);
        registApp.setModifyAt(new Date());
        appManageDAO.update(registApp);
    }
}

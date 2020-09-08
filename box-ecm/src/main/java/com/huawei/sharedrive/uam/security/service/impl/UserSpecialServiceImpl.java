package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.dao.UserSpecialDAO;
import com.huawei.sharedrive.uam.security.domain.UserSpecial;
import com.huawei.sharedrive.uam.security.service.UserSpecialService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.config.service.ConfigManager;

@Component("userSpecialService")
public class UserSpecialServiceImpl implements UserSpecialService
{
    
    @Autowired
    private UserSpecialDAO userSpecialDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public void insert(UserSpecial userSpecial)
    {
        userSpecialDAO.insert(userSpecial);
        configManager.setConfig(UserSpecialServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public void delete(UserSpecial userSpecial)
    {
        userSpecialDAO.delete(userSpecial);
        configManager.setConfig(UserSpecialServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public void update(UserSpecial userSpecial, UserSpecial oldUserSpecial)
    {
        userSpecialDAO.update(userSpecial, oldUserSpecial);
        configManager.setConfig(UserSpecialServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    public Page<UserSpecial> queryPage(UserSpecial userSpecial, PageRequest pageRequest)
    {
        int total = userSpecialDAO.getFilterdCount(userSpecial);
        List<UserSpecial> content = userSpecialDAO.getAll(userSpecial,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<UserSpecial> page = new PageImpl<UserSpecial>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public Integer isExist(UserSpecial userSpecial)
    {
        return userSpecialDAO.isExist(userSpecial);
    }
    
    @Override
    public UserSpecial getUserSpecial(UserSpecial userSpecial)
    {
        return userSpecialDAO.getUserSpecial(userSpecial);
    }
    
    @Override
    public UserSpecial getUserByNameAndType(String userName, Integer specialType)
    {
        return userSpecialDAO.getUserByNameAndType(userName, specialType);
    }
}

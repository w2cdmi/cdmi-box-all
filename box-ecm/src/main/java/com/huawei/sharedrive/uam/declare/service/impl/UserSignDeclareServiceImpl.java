package com.huawei.sharedrive.uam.declare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.declare.dao.UserSignDeclareDao;
import com.huawei.sharedrive.uam.declare.service.UserSignDeclareService;

import pw.cdmi.common.domain.UserSignDeclare;

@Service
public class UserSignDeclareServiceImpl implements UserSignDeclareService
{
    @Autowired
    private UserSignDeclareDao userSignDeclareDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void create(UserSignDeclare declare)
    {
        delete(declare);
        userSignDeclareDao.create(declare);
    }
    
    @Override
    public UserSignDeclare getUserSignDeclare(UserSignDeclare declare)
    {
        return userSignDeclareDao.getUserSignDeclare(declare);
    }
    
    @Override
    public void delete(UserSignDeclare declare)
    {
        userSignDeclareDao.delete(declare);
    }
    
    @Override
    public UserSignDeclare getUserSignByClientType(UserSignDeclare declare)
    {
        return userSignDeclareDao.getUserSignByClientType(declare);
    }
    
}

package com.huawei.sharedrive.isystem.common.systemtask.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.common.systemtask.dao.UserDBInfoDAO;
import com.huawei.sharedrive.isystem.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.isystem.common.systemtask.service.UserDBInfoService;

@Service("userDBInfoService")
public class UserDBInfoServiceImpl implements UserDBInfoService
{
    @Autowired
    private UserDBInfoDAO userDBInfoDAO;
    
    @Override
    public List<UserDBInfo> listAll()
    {
        return userDBInfoDAO.listAllUserdbInfo();
    }
    
}

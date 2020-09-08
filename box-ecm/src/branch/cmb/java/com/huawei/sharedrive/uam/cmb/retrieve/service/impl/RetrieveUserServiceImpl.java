package com.huawei.sharedrive.uam.cmb.retrieve.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.retrieve.dao.RetrieveUserDao;
import com.huawei.sharedrive.uam.cmb.retrieve.domain.RetrieveUser;
import com.huawei.sharedrive.uam.cmb.retrieve.service.RetrieveUserService;
import com.huawei.sharedrive.uam.core.domain.Limit;

@Service
public class RetrieveUserServiceImpl implements RetrieveUserService
{
    @Autowired
    private RetrieveUserDao retrieveUserDao;
    
    @Override
    public int getRetrieveUserCount(String filter, Integer orgId, Long accountId, Long enterpriseId)
    {
        return retrieveUserDao.getRetrieveUserCount(filter, orgId, accountId, enterpriseId);
    }
    
    @Override
    public List<RetrieveUser> getRetrieveUserList(String filter, Integer orgId, Long accountId,
        Long enterpriseId, Limit limit)
    {
        return retrieveUserDao.getRetrieveUserList(filter, orgId, accountId, enterpriseId, limit);
    }
}

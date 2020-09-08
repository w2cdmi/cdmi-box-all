package com.huawei.sharedrive.uam.cmb.retrieve.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseRetriewUser;
import com.huawei.sharedrive.uam.cmb.retrieve.domain.RetrieveUser;
import com.huawei.sharedrive.uam.cmb.retrieve.manager.UserRetrieveManager;
import com.huawei.sharedrive.uam.cmb.retrieve.service.RetrieveUserService;
import com.huawei.sharedrive.uam.core.domain.Limit;

@Component
public class UserRetrieveManagerImpl implements UserRetrieveManager
{
    @Autowired
    private RetrieveUserService retrieveUserService;
    
    @Override
    public ResponseRetriewUser getUserByOrgId(String filter, Integer orgId, Long accountId,
        Long enterpriseId, Limit limit)
    {
        int totalCount = retrieveUserService.getRetrieveUserCount(filter, orgId, accountId, enterpriseId);
        List<RetrieveUser> list = retrieveUserService.getRetrieveUserList(filter,
            orgId,
            accountId,
            enterpriseId,
            limit);
        ResponseRetriewUser responseRetriewUser = new ResponseRetriewUser();
        responseRetriewUser.setLimit(limit.getLength());
        responseRetriewUser.setOffset(limit.getOffset());
        responseRetriewUser.setTotalCount((long) totalCount);
        responseRetriewUser.setUsers(list);
        return responseRetriewUser;
    }
}

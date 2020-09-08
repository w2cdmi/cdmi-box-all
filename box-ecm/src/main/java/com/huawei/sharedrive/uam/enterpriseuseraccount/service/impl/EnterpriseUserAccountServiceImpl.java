package com.huawei.sharedrive.uam.enterpriseuseraccount.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuseraccount.dao.EnterpriseUserAccountDao;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.enterpriseuseraccount.service.EnterpriseUserAccountService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class EnterpriseUserAccountServiceImpl implements EnterpriseUserAccountService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserAccountService.class);
    
    @Autowired
    private EnterpriseUserAccountDao enterpriseUserAccountDao;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Resource
    private RestClient ufmClientService;
    
    @Override
    public List<ResponseUser> listUser(String appId, long accountid, long enterpriseId, String filter,
        Limit limit)
    {
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise)
        {
            LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
            throw new BusinessException();
        }
        List<EnterpriseUserAccount> content = enterpriseUserAccountDao.getUser(accountid,
            enterpriseId,
            filter,
            limit);
        UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
        EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountid);
        if (null == enterpriseAccount)
        {
            LOGGER.error("enterpriseAccount is null accountId:" + accountid);
            throw new BusinessException();
        }
        List<ResponseUser> responseUserList = new ArrayList<ResponseUser>(10);
        ResponseUser responseUser;
        RestUserCreateRequest restUserCreateRequest;
        for (EnterpriseUserAccount user : content)
        {
            responseUser = ResponseUser.convetToResponseUser(user, appId);
            restUserCreateRequest = userHttpClient.getUserInfo(user.getCloudUserId(),
                enterpriseAccount);
            if (restUserCreateRequest != null)
            {
                user.setSpaceUsed(restUserCreateRequest.getSpaceUsed());
                user.setFileCount(restUserCreateRequest.getFileCount());
            }
            responseUser.setAppId(appId);
            responseUser.setDomain(enterprise.getDomainName());
            responseUserList.add(responseUser);
        }
        return responseUserList;
    }
    
    @Override
    public int listUserCount(long accountid, long enterpriseId, String filter, Limit limit)
    {
        int total = enterpriseUserAccountDao.getUserCount(accountid, enterpriseId, filter, limit);
        return total;
    }
}

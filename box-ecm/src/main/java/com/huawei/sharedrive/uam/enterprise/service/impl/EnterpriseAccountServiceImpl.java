package com.huawei.sharedrive.uam.enterprise.service.impl;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseAccountDao;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.httpclient.rest.EnterpriseHttpClient;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnterpriseAccountServiceImpl implements EnterpriseAccountService {
    private static Logger logger = LoggerFactory.getLogger(EnterpriseAccountServiceImpl.class);

    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private RestClient ufmClientService;

    @Override
    public List<EnterpriseAccount> getByEnterpriseId(long enterpriseId) {

        return enterpriseAccountDao.getByEnterpriseId(enterpriseId);
    }

    @Override
    public int deleteByAccountId(long accountId) {

        return enterpriseAccountDao.deleteByAccountId(accountId);
    }

    @Override
    public void create(EnterpriseAccount enterpriseAccount) {

        enterpriseAccountDao.create(enterpriseAccount);
    }

    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId) {
        return enterpriseAccountDao.getByEnterpriseApp(enterpriseId, authAppId);
    }

    @Override
    public List<String> getAppByEnterpriseId(long enterpriseId) {

        return enterpriseAccountDao.getAppByEnterpriseId(enterpriseId);
    }

    @Override
    public List<Long> getAccountIdByEnterpriseId(long enterpriseId) {

        return enterpriseAccountDao.getAccountIdByEnterpriseId(enterpriseId);
    }

    @Override
    public List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId) {

        return enterpriseAccountDao.getAppContextByEnterpriseId(enterpriseId);
    }

    @Override
    public EnterpriseAccount getByAccessKeyId(String accessKeyId) {
        return enterpriseAccountDao.getByAccessKeyId(accessKeyId);
    }

    @Override
    public EnterpriseAccount getByAccountId(long accountId) {
        return enterpriseAccountDao.getByAccountId(accountId);
    }

    //密码复杂度
    @Override
    public void setPwdLevelByEnterpriseId(long enterpriseId, int pwdLevel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void modifyPwdLevelByEnterpriseId(long enterpriseId, int pwdLevel) {
        enterpriseAccountDao.modifyPwdLevelByEnterpriseId(enterpriseId, pwdLevel);
    }

    @Override
    public String getPwdLevelByEnterpriseId(long enterpriseId) {
        return enterpriseAccountDao.getPwdLevelByEnterpriseId(enterpriseId);
    }

    @Override
    public void update(EnterpriseAccount enterpriseAccount) {
        // TODO Auto-generated method stub
        enterpriseAccountDao.update(enterpriseAccount);

    }
}

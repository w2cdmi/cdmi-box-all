package com.huawei.sharedrive.app.account.mamager.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountAccessKey;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.account.mamager.AccountManager;
import com.huawei.sharedrive.app.account.service.AccountAccessKeyService;
import com.huawei.sharedrive.app.account.service.AccountIdService;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchAccountException;
import com.huawei.sharedrive.app.openapi.domain.account.RestAccessKey;
import com.huawei.sharedrive.app.openapi.domain.account.RestAccount;
import com.huawei.sharedrive.app.openapi.domain.account.RestCreateAccountRequest;
import com.huawei.sharedrive.app.openapi.domain.account.RestModifyAccountRequest;
import com.huawei.sharedrive.app.utils.RandomKeyGUID;

@Component
public class AccountManagerImpl implements AccountManager
{
    public static final Logger LOGGER = LoggerFactory.getLogger(AccountManagerImpl.class);
    
    @Autowired
    private AccountAccessKeyService accessKeyService;
    
    @Autowired
    private AccountIdService accountIdService;
    
    @Autowired
    private AccountService accountService;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RestAccount create(RestCreateAccountRequest accountRequest, String appId)
    {
        long accountId = accountIdService.getNextAccountId();
        Account account = transAccount(accountRequest, appId, accountId);
        accountService.create(account);
        String accessId = RandomKeyGUID.getSecureRandomGUID();
        String secretKey = RandomKeyGUID.getSecureRandomGUID();
        AccountAccessKey accessKey = createAccessKey(accountId, accessId, secretKey);
        accessKey.setSecretKey(secretKey);
       
        RestAccount restAccount = transAccount(account, accessKey);
        return restAccount;
    }
    
    private AccountAccessKey createAccessKey(long accountId, String accessId, String secretKey)
    {
        AccountAccessKey accessKey = new AccountAccessKey();
        accessKey.setAccountId(accountId);
        accessKey.setId(accessId);
        accessKey.setCreatedAt(new Date());
        accessKey.setSecretKey(secretKey);
        accessKeyService.create(accessKey);
        return accessKey;
    }
    
    @Override
    public Account getById(long id)
    {
        Account account = accountService.getById(id);
        if (account == null)
        {
            throw new NoSuchAccountException("cant not found account " + id);
        }
        return account;
    }
    
    @Override
    public RestAccount getRestAccountById(long id)
    {
        Account account = accountService.getById(id);
        if (account == null)
        {
            throw new NoSuchAccountException("cant not found account " + id);
        }
        RestAccount restAccount = transAccount(account, null);
        return restAccount;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RestAccount modify(RestModifyAccountRequest accountRequest, String appId, Long accountId,
        String authorization)
    {
        Account account = getById(accountId);
        if (!account.getAppId().equals(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to do this");
        }
        transModify(accountRequest, account);
        account.lossToMax();
        accountService.update(account);

        RestAccount restAccount = transAccount(account, null);
        return restAccount;
    }
    
    private RestAccount transAccount(Account account, AccountAccessKey accessKey)
    {
        RestAccount restAccount = new RestAccount(account);
        if (accessKey != null)
        {
            restAccount.setAccessToken(new RestAccessKey(accessKey));
        }
        return restAccount;
    }
    
    private Account transAccount(RestCreateAccountRequest accountRequest, String appId, long accountId)
    {
        Account account = new Account();
        account.setId(accountId);
        account.setAppId(appId);
        account.setDomain(accountRequest.getDomain());
        account.setStatus(transStatus(accountRequest.getStatus()));
        account.setEnterpriseId(accountRequest.getEnterpriseId());
        account.setFilePreviewable(accountRequest.getFilePreviewable());
        account.setFileScanable(accountRequest.getFileScanable());
        transNum(accountRequest, account);
        Date date = new Date();
        account.setCreatedAt(date);
        account.setModifiedAt(date);
        account.setCurrentFile(0L);
        account.setCurrentMember(0);
        account.setCurrentSpace(0L);
        account.setCurrentTeamspace(0);
        return account;
    }
    
    private void transNum(RestCreateAccountRequest accountRequest, Account account)
    {
        
        if (accountRequest.getMaxSpace() == AccountConstants.UNLIMIT_NUM)
        {
            account.setMaxSpace(AccountConstants.UNLIMIT_NUM_RESTORE_SPACE);
        }
        else
        {
            account.setMaxSpace(accountRequest.getMaxSpace());
        }
        
        if (accountRequest.getMaxMember() == AccountConstants.UNLIMIT_NUM)
        {
            account.setMaxMember(AccountConstants.UNLIMIT_NUM_RESTORE);
        }
        else
        {
            account.setMaxMember(accountRequest.getMaxMember());
        }
        if (accountRequest.getMaxTeamspace() == AccountConstants.UNLIMIT_NUM)
        {
            account.setMaxTeamspace(AccountConstants.UNLIMIT_NUM_RESTORE);
        }
        else
        {
            account.setMaxTeamspace(accountRequest.getMaxTeamspace());
        }
    }
    
    private void transModify(RestModifyAccountRequest accountRequest, Account account)
    {
        if (accountRequest.getDomain() != null)
        {
            account.setDomain(accountRequest.getDomain());
        }
        if (accountRequest.getMaxMember() != null)
        {
            account.setMaxMember(accountRequest.getMaxMember());
        }
        if (accountRequest.getMaxSpace() != null)
        {
            account.setMaxSpace(accountRequest.getMaxSpace());
        }
        if (accountRequest.getMaxTeamspace() != null)
        {
            account.setMaxTeamspace(accountRequest.getMaxTeamspace());
        }
        if (accountRequest.isFilePreviewable() != null)
        {
            account.setFilePreviewable(accountRequest.isFilePreviewable());
        }
        if (accountRequest.isFileScanable() != null)
        {
            account.setFileScanable(accountRequest.isFileScanable());
        }
        if (StringUtils.isNotEmpty(accountRequest.getStatus()))
        {
            account.setStatus(transStatus(accountRequest.getStatus()));
        }
        account.setModifiedAt(new Date());
    }
    
    private byte transStatus(String status)
    {
        if (StringUtils.equals(status, AccountConstants.STATUS_DIS))
        {
            return AccountConstants.STATUS_DISABLE;
        }
        return AccountConstants.STATUS_ENABLE;
    }
}

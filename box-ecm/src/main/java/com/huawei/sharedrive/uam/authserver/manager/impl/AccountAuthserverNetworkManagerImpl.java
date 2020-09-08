package com.huawei.sharedrive.uam.authserver.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverNetworkManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverNetworkService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.IpUtils;

@Component
public class AccountAuthserverNetworkManagerImpl implements AccountAuthserverNetworkManager
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountAuthserverNetworkManagerImpl.class);
    
    public final static String SPLIT_COMMA = ",";
    
    @Autowired
    private AccountAuthserverNetworkService accountAuthserverNetworkService;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Override
    public Page<AccountAuthserverNetwork> listNetwork(Long authServerId, Long accountId,
        PageRequest pageRequest)
    {
        int total = accountAuthserverNetworkService.getNetworkCount(authServerId, accountId);
        List<AccountAuthserverNetwork> content = accountAuthserverNetworkService.getNetworkList(authServerId,
            accountId,
            pageRequest.getLimit());
        AccountAuthserverNetwork.htmlEscape(content);
        Page<AccountAuthserverNetwork> page = new PageImpl<AccountAuthserverNetwork>(content, pageRequest,
            total);
        return page;
    }
    
    @Override
    public void create(Long accountId, Long authServerId, String ipStart, String ipEnd)
    {
        if (null == accountId || null == authServerId)
        {
            LOGGER.error("accountId or authServerId is null accountId:" + accountId + " authServerId:"
                + authServerId);
            throw new InvalidParamterException("accountId or authServerId is null");
        }
        checkNetworkIp(ipStart, ipEnd);
        long ipStartValue = IpUtils.toLong(ipStart);
        long ipEndValue = IpUtils.toLong(ipEnd);
        List<AccountAuthserverNetwork> content = accountAuthserverNetworkService.getNetworkList(authServerId,
            accountId,
            null);
        checkIp(content, ipStartValue, ipEndValue);
        AccountAuthserverNetwork accountAuthserverNetwork = new AccountAuthserverNetwork();
        accountAuthserverNetwork.setAccountId(accountId);
        accountAuthserverNetwork.setAuthServerId(authServerId);
        accountAuthserverNetwork.setIpStart(ipStart);
        accountAuthserverNetwork.setIpEnd(ipEnd);
        accountAuthserverNetwork.setIpStartValue(ipStartValue);
        accountAuthserverNetwork.setIpEndValue(ipEndValue);
        accountAuthserverNetworkService.create(accountAuthserverNetwork);
    }
    
    @Override
    public void update(Long id, String ipStart, String ipEnd)
    {
        if (null == id)
        {
            LOGGER.error("id is null");
            throw new InvalidParamterException("id is null");
        }
        AccountAuthserverNetwork accountAuthserverNetwork = accountAuthserverNetworkService.getById(id);
        if (null == accountAuthserverNetwork)
        {
            LOGGER.error("accountAuthserverNetwork is null id:" + id);
            throw new InvalidParamterException("accountAuthserverNetwork is null");
        }
        checkNetworkIp(ipStart, ipEnd);
        List<AccountAuthserverNetwork> content = accountAuthserverNetworkService.getNetworkList(accountAuthserverNetwork.getAuthServerId(),
            accountAuthserverNetwork.getAccountId(),
            null);
        long ipStartValue = IpUtils.toLong(ipStart);
        long ipEndValue = IpUtils.toLong(ipEnd);
        
        checkIp(content, ipStartValue, ipEndValue, id);
        accountAuthserverNetwork.setIpStart(ipStart);
        accountAuthserverNetwork.setIpStartValue(ipStartValue);
        accountAuthserverNetwork.setIpEnd(ipEnd);
        accountAuthserverNetwork.setIpEndValue(ipEndValue);
        accountAuthserverNetworkService.update(accountAuthserverNetwork);
    }
    
    @Override
    public void deleteByIds(String ids)
    {
        if (StringUtils.isBlank(ids))
        {
            return;
        }
        accountAuthserverNetworkService.deleteByIds(ids);
    }
    
    @Override
    public void deleteAll(Long authServerId, Long accountId)
    {
        if (null == accountId || null == authServerId)
        {
            LOGGER.error("accountId or authServerId is null accountId:" + accountId + " authServerId:"
                + authServerId);
            throw new InvalidParamterException("accountId or authServerId is null");
        }
        accountAuthserverNetworkService.deleteAll(authServerId, accountId);
    }
    
    @Override
    public void checkNetworkIp(String ipStart, String ipEnd)
    {
        if (StringUtils.isBlank(ipStart) || StringUtils.isBlank(ipEnd))
        {
            LOGGER.error("ipStart or ipEnd is null ipStart:" + ipStart + " ipEnd:" + ipEnd);
            throw new InvalidParamterException("ipStart or ipEnd is null");
        }
        long ipStartValue = IpUtils.toLong(ipStart);
        long ipEndValue = IpUtils.toLong(ipEnd);
        if (IpUtils.ERROR_IP == ipStartValue || IpUtils.ERROR_IP == ipEndValue)
        {
            LOGGER.error("ipStart or ipEnd is invalid ipStart:" + ipStart + " ipEnd:" + ipEnd);
            throw new InvalidParamterException("ipStart or ipEnd is invalid");
        }
        if (ipStartValue >= ipEndValue)
        {
            LOGGER.error("ipStart is larger than ipEnd ipStart:" + ipStart + " ipEnd:" + ipEnd);
            throw new InvalidParamterException("ipStart is larger than ipEnd");
        }
    }
    
    @Override
    public AuthServer checkAndGetAuthServerId(String realIp, Long accountId)
    {
        AuthServer authServer = null;
        long realIpL = checkAndGetIp(realIp);
        if (null == accountId)
        {
            LOGGER.error("accountId is null");
            throw new InvalidParamterException("accountId is null ");
        }
        List<AccountAuthserverNetwork> content = accountAuthserverNetworkService.getNetworkListByAccount(accountId);
        if (CollectionUtils.isNotEmpty(content))
        {
            long selIpStartValue;
            long selIpEndValue;
            for (AccountAuthserverNetwork accountAuthserverNetwork : content)
            {
                selIpStartValue = accountAuthserverNetwork.getIpStartValue();
                selIpEndValue = accountAuthserverNetwork.getIpEndValue();
                if (realIpL > selIpStartValue && realIpL < selIpEndValue)
                {
                    authServer = authServerManager.getAuthServer(accountAuthserverNetwork.getAuthServerId());
                    break;
                }
            }
        }
        return authServer;
    }
    
    private void checkIp(List<AccountAuthserverNetwork> content, long ipStartValue, long ipEndValue)
    {
        if (CollectionUtils.isEmpty(content))
        {
            return;
        }
        long selIpStartValue;
        long selIpEndValue;
        for (AccountAuthserverNetwork accountAuthserverNetwork : content)
        {
            selIpStartValue = accountAuthserverNetwork.getIpStartValue();
            selIpEndValue = accountAuthserverNetwork.getIpEndValue();
            if (ipStartValue < selIpStartValue && ipEndValue > selIpStartValue)
            {
                LOGGER.error("repeat network ipStartValue:" + ipStartValue + " ipEndValue:" + ipEndValue
                    + " selIpStartValue:" + selIpStartValue + " selIpEndValue:" + selIpEndValue);
                throw new InvalidParamterException("repeat network");
            }
            if (ipStartValue >= selIpStartValue && ipStartValue <= selIpEndValue)
            {
                LOGGER.error("repeat network ipStartValue:" + ipStartValue + " ipEndValue:" + ipEndValue
                    + " selIpStartValue:" + selIpStartValue + " selIpEndValue:" + selIpEndValue);
                throw new InvalidParamterException("repeat network");
            }
        }
    }
    
    private void checkIp(List<AccountAuthserverNetwork> content, long ipStartValue, long ipEndValue, Long id)
    {
        if (CollectionUtils.isEmpty(content))
        {
            return;
        }
        long selIpStartValue;
        long selIpEndValue;
        for (AccountAuthserverNetwork accountAuthserverNetwork : content)
        {
            if (id == accountAuthserverNetwork.getId())
            {
                continue;
            }
            selIpStartValue = accountAuthserverNetwork.getIpStartValue();
            selIpEndValue = accountAuthserverNetwork.getIpEndValue();
            if (ipStartValue < selIpStartValue && ipEndValue > selIpStartValue)
            {
                LOGGER.error("repeat network ipStartValue:" + ipStartValue + " ipEndValue:" + ipEndValue
                    + " selIpStartValue:" + selIpStartValue + " selIpEndValue:" + selIpEndValue);
                throw new InvalidParamterException("repeat network");
            }
            if (ipStartValue >= selIpStartValue && ipStartValue <= selIpEndValue)
            {
                LOGGER.error("repeat network ipStartValue:" + ipStartValue + " ipEndValue:" + ipEndValue
                    + " selIpStartValue:" + selIpStartValue + " selIpEndValue:" + selIpEndValue);
                throw new InvalidParamterException("repeat network");
            }
        }
    }
    
    private long checkAndGetIp(String realIp)
    {
        long realIpL = IpUtils.toLong(realIp);
        if (IpUtils.ERROR_IP == realIpL)
        {
            LOGGER.error("invalid realIp realIp:" + realIp);
            throw new InvalidParamterException("invalid realIp");
        }
        return realIpL;
    }
}

package com.huawei.sharedrive.uam.openapi.manager.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.openapi.manager.NtlmApiCheckManager;
import com.huawei.sharedrive.uam.terminal.service.impl.TerminalServiceImpl;

@Component
public class NtlmApiCheckManagerImpl implements NtlmApiCheckManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NtlmApiCheckManager.class);
    
    @Override
    public void checkNtlmParamter(String appId, String key, String challenge, HttpServletRequest request)
        throws InvalidParamterException
    {
        if (StringUtils.isBlank(challenge) || StringUtils.isBlank(key) || StringUtils.isBlank(appId))
        {
            LOGGER.error("invalidate parameter challenge:" + challenge + " key:" + key + " appId:" + appId);
            throw new InvalidParamterException();
        }
        TerminalServiceImpl.checkDeviceParamter(request);
    }
}

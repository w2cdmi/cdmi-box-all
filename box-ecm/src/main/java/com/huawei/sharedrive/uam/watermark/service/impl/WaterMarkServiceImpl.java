package com.huawei.sharedrive.uam.watermark.service.impl;

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.httpclient.rest.WatermarkHttpClient;
import com.huawei.sharedrive.uam.watermark.service.WaterMarkServcie;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class WaterMarkServiceImpl implements WaterMarkServcie
{
    public static final Logger LOGGER = LoggerFactory.getLogger(WaterMarkServiceImpl.class);
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Override
    public void putWaterMark(long accoutId, InputStream in, int imagelength)
    {
        try
        {
            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accoutId);
            if (null == enterpriseAccount)
            {
                throw new InternalServerErrorException("account is null");
            }
            WatermarkHttpClient watermarkHttpClient = new WatermarkHttpClient(ufmClientService);
            AuthApp authApp = authAppService.getByAuthAppID(enterpriseAccount.getAuthAppId());
            watermarkHttpClient.putWaterMark(enterpriseAccount, in, imagelength, authApp);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
    
    @Override
    public byte[] getWaterMark(long accoutId)
    {
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accoutId);
        if (null == enterpriseAccount)
        {
            return new byte[]{};
        }
        WatermarkHttpClient watermarkHttpClient = new WatermarkHttpClient(ufmClientService);
        AuthApp authApp = authAppService.getByAuthAppID(enterpriseAccount.getAuthAppId());
        return watermarkHttpClient.getWaterMark(enterpriseAccount, authApp);
        
    }
}

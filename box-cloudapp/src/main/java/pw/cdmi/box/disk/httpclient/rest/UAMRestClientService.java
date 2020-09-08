package pw.cdmi.box.disk.httpclient.rest;

import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.disk.system.service.AccessAddressService;

public class UAMRestClientService extends AbstractRestClientService
{
    @Autowired
    private AccessAddressService accessAddressService;

    protected String getAppToken()
    {
    return accessAddressService.getAccessAddress().getAppId();
    }

    @Override
    protected String getServerUrl()
    {
    return accessAddressService.getAccessAddress().getUamInnerAddress();
    }
}

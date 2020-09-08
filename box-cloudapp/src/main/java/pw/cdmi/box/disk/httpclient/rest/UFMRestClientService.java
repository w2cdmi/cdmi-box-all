package pw.cdmi.box.disk.httpclient.rest;

import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.disk.system.service.AccessAddressService;

public abstract class UFMRestClientService extends AbstractRestClientService
{
    
    @Autowired
    private AccessAddressService accessAddressService;
    
    protected String getAppToken()
    {
        return "";
    }
    
    @Override
    protected String getServerUrl()
    {
        
        return accessAddressService.getAccessAddress().getUfmInnerAddress();
    }
}

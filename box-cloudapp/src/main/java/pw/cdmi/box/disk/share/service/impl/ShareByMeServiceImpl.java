package pw.cdmi.box.disk.share.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.MySharesClient;
import pw.cdmi.box.disk.share.domain.MySharesPage;
import pw.cdmi.box.disk.share.domain.RestListShareResourceRequestV2;
import pw.cdmi.box.disk.share.service.ShareByMeService;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class ShareByMeServiceImpl implements ShareByMeService
{
    
    @Resource
    private RestClient ufmClientService;
    
    @Override
    public MySharesPage getMySharesPage(String token, RestListShareResourceRequestV2 req)
        throws RestException
    {
        
        return new MySharesClient(ufmClientService).getMySharesPage(token, req);
    }
    
}

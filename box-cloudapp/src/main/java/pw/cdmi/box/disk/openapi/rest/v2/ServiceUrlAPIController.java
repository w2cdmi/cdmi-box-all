package pw.cdmi.box.disk.openapi.rest.v2;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.disk.system.service.AccessAddressService;
import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.core.exception.AuthFailedException;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Controller
@RequestMapping(value = "/api/v2/serverurl")
public class ServiceUrlAPIController
{
    private final static String SERVER_UAM = "ecm";
    
    private final static String SERVER_UFM = "ufm";
    
    @Autowired
    private AccessAddressService accessAddressService;
    
    @Resource
    private RestClient uamClientService;
    
    /**
     * 
     * @param userlogin
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ServiceUrlResponse> getServerUrl(@RequestHeader("Authorization") String token,
        String type) throws BaseRunException
    {
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        TextResponse result = uamClientService.performGetText("/api/v2/users/me", headerMap);
        if (result.getStatusCode() != HttpStatus.OK.value())
        {
            throw new AuthFailedException();
        }
        ServiceUrlResponse response = new ServiceUrlResponse();
        AccessAddressConfig accessConfig = accessAddressService.getAccessAddress();
        if (SERVER_UAM.equals(type))
        {
            response.setServerUrl(accessConfig.getUamOuterAddress());
        }
        else if (SERVER_UFM.equals(type))
        {
            response.setServerUrl(accessConfig.getUfmOuterAddress());
        }
        else
        {
            throw new InvalidParamException();
        }
        return new ResponseEntity<ServiceUrlResponse>(response, HttpStatus.OK);
    }
    
}

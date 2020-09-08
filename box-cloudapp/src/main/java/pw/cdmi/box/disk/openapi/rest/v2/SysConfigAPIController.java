package pw.cdmi.box.disk.openapi.rest.v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.oauth2.client.UserTokenHelper;
import pw.cdmi.box.disk.openapi.rest.v2.domain.RestConfigInfo;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.NoSuchOptionException;

@Controller
@RequestMapping(value = "/api/v2/config")
public class SysConfigAPIController
{
    private static Logger logger = LoggerFactory.getLogger(SysConfigAPIController.class);
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    /**
     * Access to the system configuration
     * 
     * @param token
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getSecurityConfig(String option, @RequestHeader("Authorization") String token)
    {
        userTokenHelper.checkTokenAndGetUserForV2(token);
        
        if (StringUtils.isBlank(option))
        {
            throw new InvalidParamException();
        }
        
        SecurityConfig securityConfig = securityService.getSecurityConfig();
        
        List<RestConfigInfo> configList = new ArrayList<RestConfigInfo>(1);
        if ("linkAccessKeyRule".equals(option))
        {
            configList.add(new RestConfigInfo(option,
                String.valueOf(securityConfig.isDisableSimpleLinkCode())));
        }
        else if ("all".equals(option))
        {
            configList.add(new RestConfigInfo(option,
                String.valueOf(securityConfig.isDisableSimpleLinkCode())));
        }
        else
        {
            throw new NoSuchOptionException("option is not found:" + option);
        }
        
        logger.info(ToStringBuilder.reflectionToString(configList));
        return new ResponseEntity<List<RestConfigInfo>>(configList, HttpStatus.OK);
    }
}

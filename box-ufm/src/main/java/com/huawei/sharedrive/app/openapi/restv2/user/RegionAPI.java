/**
 * 
 */
package com.huawei.sharedrive.app.openapi.restv2.user;

import java.util.List;

import io.swagger.annotations.Api;
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

import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;

/**
 * 
 * @author h90005572
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/regions")
@Api(hidden = true)
public class RegionAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionAPI.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private SystemTokenHelper systemTokenHelper;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    /**
     * 列举存储区域
     * 
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Region>> getRegionList(@RequestHeader("Authorization") String authorization,
        @RequestHeader("Date") String date) throws BaseRunException
    {
        UserToken userToken = new UserToken();
        String[] akArray = authorization.split(",");
        if (akArray.length >= 2)
        {
            userToken.setLoginName(akArray[1]);
        }
        List<Region> listRegion = null;
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX))
        {
            userTokenHelper.checkAccountToken(authorization, date);
        }
        else if (authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            userTokenHelper.checkAccountToken(authorization, date);
        }
        else if (authorization.startsWith(SystemTokenHelper.AUTH_SYSTEM))
        {
            systemTokenHelper.checkSystemToken(authorization, date);
        }
        else if (authorization.startsWith(UserTokenHelper.APPLICATION_PREFIX))
        {
            String appId = userTokenHelper.checkAppSystemToken(authorization, date);
            LOGGER.debug(appId);
        }
        else
        {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
        }
        try
        {
            listRegion = regionService.listRegion();
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_REGION_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_REGION,
            null,
            null);
        return new ResponseEntity<List<Region>>(listRegion, HttpStatus.OK);
    }
}

package com.huawei.sharedrive.uam.openapi.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;
import com.huawei.sharedrive.uam.accountrole.service.AccountRoleService;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.teamspace.domain.RestNodeRoleInfo;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;

import pw.cdmi.common.log.UserLog;

/**
 * 
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/roles")
public class AccountNodeRoleApi
{
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private AccountRoleService accountRoleService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private UserLogService userLogService;
    
    /**
     * 
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RestNodeRoleInfo>> list(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest req)
        throws BaseRunException
    {
        List<RestNodeRoleInfo> accountNodeRoles = null;
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserLog userLog = UserLogType.getUserLog(userToken);
        try
        {
            
            List<RestNodeRoleInfo> roleList = teamSpaceService.getSystemRoles(userToken.getAccountId());
            if (roleList == null)
            {
                return new ResponseEntity<List<RestNodeRoleInfo>>(new ArrayList<RestNodeRoleInfo>(0),
                    HttpStatus.OK);
            }
            List<AccountRole> accountRoles = accountRoleService.getList(userToken.getAccountId());
            
            accountNodeRoles = new ArrayList<RestNodeRoleInfo>(accountRoles.size());
            
            for (AccountRole accountRole : accountRoles)
            {
                for (RestNodeRoleInfo item : roleList)
                {
                    if (StringUtils.equals(item.getName(), accountRole.getResourceRole()))
                    {
                        accountNodeRoles.add(item);
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            // TODO Auto-generated catch block
            userLogService.saveFailLog(userLog, UserLogType.KEY_LIST_ROLES_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_LIST_ROLES, null);
        return new ResponseEntity<List<RestNodeRoleInfo>>(accountNodeRoles, HttpStatus.OK);
    }
    
}

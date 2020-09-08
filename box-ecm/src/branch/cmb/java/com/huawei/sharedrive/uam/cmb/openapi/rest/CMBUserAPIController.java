package com.huawei.sharedrive.uam.cmb.openapi.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.cmb.openapi.domain.RequestRetriewUser;
import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseRetriewUser;
import com.huawei.sharedrive.uam.cmb.retrieve.manager.UserRetrieveManager;
import com.huawei.sharedrive.uam.core.domain.Limit;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;

@Controller
@RequestMapping(value = "/api/v2/cmb/user")
public class CMBUserAPIController
{
    private static Logger logger = LoggerFactory.getLogger(CMBUserAPIController.class);
    
    public static final int MAX_FATHER_GROUP_ID = 11;
    
    private static final int DEFAULT_LIMIT = 10000;
    
    private static final int DEFAULT_OFFSET = 0;
    
    private static final int MAX_LIMIT = 10000;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private UserRetrieveManager userRetrieveManager;
    
    /**
     * 
     * @param userId
     * @return offset
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseRetriewUser> search(@RequestHeader String authorization,
        @RequestBody RequestRetriewUser requestRetriewUser) throws BaseRunException
    {
        checkParameter(requestRetriewUser);
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        String filter = requestRetriewUser.getKeyword();
        Integer orgId = requestRetriewUser.getOrgId();
        Integer limit = requestRetriewUser.getLimit();
        Integer offset = requestRetriewUser.getOffset();
        Long accountId = userToken.getAccountId();
        Long enterpriseId = userToken.getEnterpriseId();
        Limit limitObject = new Limit();
        limitObject.setLength(limit);
        limitObject.setOffset(offset);
        ResponseRetriewUser responseRetriewUser = userRetrieveManager.getUserByOrgId(filter,
            orgId,
            accountId,
            enterpriseId,
            limitObject);
        return new ResponseEntity<ResponseRetriewUser>(responseRetriewUser, HttpStatus.OK);
        
    }
    
    private void checkParameter(RequestRetriewUser requestRetriewUser)
    {
        Integer orgId = requestRetriewUser.getOrgId();
        
        Integer limit = requestRetriewUser.getLimit();
        
        Integer offset = requestRetriewUser.getOffset();
        if (null != orgId)
        {
            if (orgId.toString().length() > MAX_FATHER_GROUP_ID)
            {
                logger.error("fatherGroupId is too large :" + orgId);
                throw new InvalidParamterException();
            }
        }
        if (offset != null)
        {
            if (offset < 0)
            {
                logger.error("offset is negative number :" + offset);
                throw new InvalidParamterException();
            }
        }
        else
        {
            requestRetriewUser.setOffset(DEFAULT_OFFSET);
        }
        if (limit != null)
        {
            if (limit < 0 || limit > MAX_LIMIT)
            {
                logger.error("limit is out of range :" + limit);
                throw new InvalidParamterException();
            }
        }
        else
        {
            requestRetriewUser.setLimit(DEFAULT_LIMIT);
        }
    }
}

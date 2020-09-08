package com.huawei.sharedrive.cloudapp.cmb.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.cloudapp.cmb.control.CMBConstants;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.OrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RequestOrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RequestRetriewUser;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.ResponseOrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.ResponseRetriewUser;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RetrieveUser;
import com.huawei.sharedrive.cloudapp.cmb.user.service.CMBOrgUserService;
import com.huawei.sharedrive.cloudapp.exception.InternalServerErrorException;
import com.huawei.sharedrive.cloudapp.exception.RestException;
import com.huawei.sharedrive.cloudapp.share.service.impl.ShareServiceImpl;
import com.huawei.sharedrive.cloudapp.user.service.UserTokenManager;
import com.huawei.sharedrive.cloudapp.utils.JsonUtils;
import com.huawei.sharedrive.common.restrpc.RestClient;
import com.huawei.sharedrive.common.restrpc.domain.TextResponse;

@Service
public class CMBOrgUserServiceImpl implements CMBOrgUserService
{
    private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);
    
    private static final String ORG_URL = "/api/v2/cmb/org";
    
    private static final String USER_URL = "/api/v2/cmb/user";
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Override
    public List<OrgTreeNode> listOrgTree(String fatherGroupId)
    {
        RequestOrgTreeNode requestOrgTreeNode = new RequestOrgTreeNode();
        requestOrgTreeNode.setFatherGroupId(fatherGroupId);
        requestOrgTreeNode.setLimit(CMBConstants.LIST_ORG_MAX);
        requestOrgTreeNode.setOffset(0);
        TextResponse restResponse = uamClientService.performJsonPostTextResponse(ORG_URL,
            assembleToken(),
            requestOrgTreeNode);
        if (null == restResponse)
        {
            logger.error("get cmb org list failed restResponse is null");
            throw new InternalServerErrorException();
        }
        if (restResponse.getStatusCode() == HttpStatus.OK.value())
        {
            ResponseOrgTreeNode responseOrgTreeNode = JsonUtils.stringToObject(restResponse.getResponseBody(),
                ResponseOrgTreeNode.class);
            return responseOrgTreeNode.getOrgs();
        }
        
        RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(),
            RestException.class);
        logger.error("get cmb org list failed statuscode:" + restResponse.getStatusCode() + " errorCode:"
            + exception.getCode() + " message:" + exception.getMessage());
        throw exception;
        
    }
    
    @Override
    public List<RetrieveUser> listCmbUser(Integer orgId, String keyword)
    {
        RequestRetriewUser requestRetriewUser = new RequestRetriewUser();
        requestRetriewUser.setKeyword(keyword);
        requestRetriewUser.setOrgId(orgId);
        requestRetriewUser.setLimit(CMBConstants.LIST_USER_MAX);
        requestRetriewUser.setOffset(0);
        
        TextResponse restResponse = uamClientService.performJsonPostTextResponse(USER_URL,
            assembleToken(),
            requestRetriewUser);
        if (null == restResponse)
        {
            logger.error("get cmb org list failed restResponse is null");
            throw new InternalServerErrorException();
        }
        if (restResponse.getStatusCode() == HttpStatus.OK.value())
        {
            ResponseRetriewUser responseRetriewUser = JsonUtils.stringToObject(restResponse.getResponseBody(),
                ResponseRetriewUser.class);
            return responseRetriewUser.getUsers();
        }
        
        RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(),
            RestException.class);
        logger.error("get cmb org list failed statuscode:" + restResponse.getStatusCode() + " errorCode:"
            + exception.getCode() + " message:" + exception.getMessage());
        throw exception;
        
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
}

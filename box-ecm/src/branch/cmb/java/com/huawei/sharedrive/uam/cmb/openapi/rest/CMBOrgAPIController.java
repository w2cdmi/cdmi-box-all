package com.huawei.sharedrive.uam.cmb.openapi.rest;

import org.apache.commons.lang.StringUtils;
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

import com.huawei.sharedrive.uam.cmb.openapi.domain.RequestOrgTreeNode;
import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseOrgTreeNode;
import com.huawei.sharedrive.uam.cmb.retrieve.manager.OrgRetrieveManager;
import com.huawei.sharedrive.uam.core.domain.Limit;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;

@Controller
@RequestMapping(value = "/api/v2/cmb/org")
public class CMBOrgAPIController
{
    private static Logger logger = LoggerFactory.getLogger(CMBOrgAPIController.class);
    
    public static final int MAX_FATHER_GROUP_ID = 16;
    
    private static final int DEFAULT_LIMIT = 10000;
    
    private static final int DEFAULT_OFFSET = 0;
    
    private static final int MAX_LIMIT = 10000;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private OrgRetrieveManager orgRetrieveManager;
    
    /**
     * 
     * @param userId
     * @return offset
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseOrgTreeNode> search(@RequestHeader String authorization,
        @RequestBody RequestOrgTreeNode requestOrgTreeNode) throws BaseRunException
    {
        checkParameter(requestOrgTreeNode);
        userTokenHelper.checkTokenAndGetUser(authorization);
        String fatherGroupId = requestOrgTreeNode.getFatherGroupId();
        Integer limit = requestOrgTreeNode.getLimit();
        Integer offset = requestOrgTreeNode.getOffset();
        Limit limitObject = new Limit();
        limitObject.setLength(limit);
        limitObject.setOffset(offset);
        ResponseOrgTreeNode responseOrgTreeNode = orgRetrieveManager.getAllOrgInfo(fatherGroupId, limitObject);
        return new ResponseEntity<ResponseOrgTreeNode>(responseOrgTreeNode, HttpStatus.OK);
        
    }
    
    private void checkParameter(RequestOrgTreeNode requestOrgTreeNode)
    {
        String fatherGroupId = requestOrgTreeNode.getFatherGroupId();
        
        Integer limit = requestOrgTreeNode.getLimit();
        
        Integer offset = requestOrgTreeNode.getOffset();
        if (StringUtils.isNotBlank(fatherGroupId))
        {
            if (fatherGroupId.length() > MAX_FATHER_GROUP_ID)
            {
                logger.error("fatherGroupId is too large :" + fatherGroupId);
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
            requestOrgTreeNode.setOffset(DEFAULT_OFFSET);
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
            requestOrgTreeNode.setLimit(DEFAULT_LIMIT);
        }
    }
}

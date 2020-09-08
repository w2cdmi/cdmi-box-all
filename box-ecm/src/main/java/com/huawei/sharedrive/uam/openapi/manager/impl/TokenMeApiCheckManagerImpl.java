package com.huawei.sharedrive.uam.openapi.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.RequestSearchUser;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeApiCheckManager;

@Component
public class TokenMeApiCheckManagerImpl implements TokenMeApiCheckManager
{
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final int DEFAULT_OFFSET = 0;
    
    private static final int MAX_LIMIT = 1000;
    
    @Override
    public void checkSearchUserParameter(RequestSearchUser searchRequest)
    {
        String type = searchRequest.getType();
        if (StringUtils.isBlank(type))
        {
            searchRequest.setType(RequestSearchUser.TYPE_AUTO);
            type = RequestSearchUser.TYPE_AUTO;
        }
        String keyword = searchRequest.getKeyword();
        Integer limit = searchRequest.getLimit();
        
        Integer offset = searchRequest.getOffset();
        
        if (!(type.equals(RequestSearchUser.TYPE_AD_USER) || type.equals(RequestSearchUser.TYPE_SYSTEM) || type.equals(RequestSearchUser.TYPE_AUTO)))
        {
            throw new InvalidParamterException();
        }
        if (StringUtils.isBlank(keyword) || keyword.length() >= 255)
        {
            throw new InvalidParamterException();
        }
        if (type.equals(RequestSearchUser.TYPE_SYSTEM))
        {
            if (offset != null)
            {
                if (offset < 0)
                {
                    throw new InvalidParamterException();
                }
            }
            else
            {
                searchRequest.setOffset(DEFAULT_OFFSET);
            }
        }
        if (limit != null)
        {
            if (limit < 0 || limit > MAX_LIMIT)
            {
                throw new InvalidParamterException();
            }
        }
        else
        {
            searchRequest.setLimit(DEFAULT_LIMIT);
        }
    }
    
    @Override
    public void checkLdapUserUpdateReq(BasicUserUpdateRequest userUpdateRequest)
    {
        if (StringUtils.isNotBlank(userUpdateRequest.getName()))
        {
            throw new InvalidParamterException("Can not update ldap user name");
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getOldPassword()))
        {
            throw new InvalidParamterException("Can not update ldap user password");
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getNewPassword()))
        {
            throw new InvalidParamterException("Can not update ldap user password");
        }
    }
}

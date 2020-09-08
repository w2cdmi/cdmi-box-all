package com.huawei.sharedrive.app.dataserver.thrift.impl;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.oauth2.domain.DataServerToken;
import com.huawei.sharedrive.app.oauth2.domain.PreviewObjectToken;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.UserTokenService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.thrift.app2dc.OAuth2ThriftService.Iface;
import com.huawei.sharedrive.thrift.app2dc.TBusinessException;
import com.huawei.sharedrive.thrift.app2dc.TokenAuthVaild;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

public class OAuth2ThriftServiceImpl implements Iface
{
    private static Logger logger = LoggerFactory.getLogger(OAuth2ThriftServiceImpl.class);
    
    private static final String UAS_REGION_NAME = PropertiesUtils.getProperty("uas.region.name", null, PropertiesUtils.BundleName.HWIT);
    
    private static final String UAS_REGION_NAME_KEY = "uasRegionName";
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
	private UserTokenService userTokenService;
    
    @Override
    public String checkTokenAuthVaild(TokenAuthVaild tokenInfo) throws TException
    {
        LoggerUtil.regiestThreadLocalLog();
        try
        {
            DataServerToken tokenValue = userTokenHelper.checkTokenAndGetUser(tokenInfo.getToken(),
                tokenInfo.getObject(),
                AuthorityMethod.valueOf(tokenInfo.getOper()));
            if (tokenValue instanceof UserToken)
            {
                UserToken userToken = (UserToken) tokenValue;
                return buildResult(userToken);
            }
            else if (tokenValue instanceof PreviewObjectToken)
            {
                PreviewObjectToken previewObjectToken = (PreviewObjectToken) tokenValue;
                return buildResult(previewObjectToken);
            }
            else
            {
                String errorMsg = "token " + tokenInfo.getToken() + " exists, but its type is "
                    + tokenValue.getClass().getName();
                logger.error(errorMsg);
                throw new TException(errorMsg);
            }
        }
        catch (Exception e)
        {
            logger.warn("checkTokenAuthVaild faild:" + ToStringBuilder.reflectionToString(tokenInfo));
            // 更新异常需要返回给DSS
            throw new TException(
                "checkTokenAuthVaild faild:" + ToStringBuilder.reflectionToString(tokenInfo), e);
        }
        
    }
    
    private String buildResult(UserToken userToken)
    {
        Authorize realAuth = Authorize.valueOf(userToken.getAuth());
        
        HashMap<String, String> resultMap = new HashMap<String, String>(3);
        resultMap.put(UserTokenHelper.KEY_CALLBACK_TYPE, UserTokenHelper.CALLBACK_TYPE_OBJECT);
        // 设置资源拥有者ID，用于dc回调更新元数据
        resultMap.put(UserTokenHelper.KEY_CALLBACK_OWNERID, String.valueOf(realAuth.getResourceOwnerID()));
        // 设置节点最后更新时间, 用于DC流下载时填充http header的"Last-Modified"字段
        resultMap.put(UserTokenHelper.KEY_CALLBACK_LAST_MODIFIED, userToken.getNodeLastModified());
        
        if(StringUtils.isNotBlank(UAS_REGION_NAME))
        {
            resultMap.put(UAS_REGION_NAME_KEY, UAS_REGION_NAME);
        }
        
        return JsonUtils.toJson(resultMap);
    }
    
    private String buildResult(PreviewObjectToken previewObjectToken)
    {
        HashMap<String, String> resultMap = new HashMap<String, String>(4);
        resultMap.put(UserTokenHelper.KEY_CALLBACK_TYPE, UserTokenHelper.CALLBACK_TYPE_PREVIEW_OBJECT);
        // 设置SourceObjectId，用于dc回调更新元数据
        resultMap.put(UserTokenHelper.KEY_CALLBACK_SOURCE_OBJECT_ID, previewObjectToken.getSourceObjectId());
        // 设置AccountId，用于dc回调更新元数据
        resultMap.put(UserTokenHelper.KEY_CALLBACK_ACCOUNT_ID, previewObjectToken.getAccountId() + "");
        resultMap.put(UserTokenHelper.KEY_CALLBACK_CONVERT_REAL_START_TIME,
            DateUtils.getDateTime(previewObjectToken.getConvertRealStartTime()) + "");
        resultMap.put(UserTokenHelper.KEY_CALLBACK_RESOURCE_GROUP_ID, previewObjectToken.getResourceGroupId()
            + "");
        
        if(StringUtils.isNotBlank(UAS_REGION_NAME))
        {
            resultMap.put(UAS_REGION_NAME_KEY, UAS_REGION_NAME);
        }
        
        return JsonUtils.toJson(resultMap);
    }

	@Override
	public String checkDataTokenAuthVaild(TokenAuthVaild tokenInfo) throws TBusinessException, TException {
		// TODO Auto-generated method stub

        LoggerUtil.regiestThreadLocalLog();
        try
        {
            DataServerToken tokenValue = userTokenHelper.checkTokenAndGetUser(tokenInfo.getToken(),
                tokenInfo.getObject(), AuthorityMethod.valueOf(tokenInfo.getOper()),false);
            if (tokenValue instanceof UserToken)
            {
                UserToken userToken = (UserToken) tokenValue;
                return buildResult(userToken);
            }
            else if (tokenValue instanceof PreviewObjectToken)
            {
                PreviewObjectToken previewObjectToken = (PreviewObjectToken) tokenValue;
                return buildResult(previewObjectToken);
            }
            else
            {
                String errorMsg = "token " + tokenInfo.getToken() + " exists, but its type is "
                    + tokenValue.getClass().getName();
                logger.error(errorMsg);
                throw new TException(errorMsg);
            }
        }
        catch (Exception e)
        {
            logger.warn("checkTokenAuthVaild faild:" + ToStringBuilder.reflectionToString(tokenInfo));
            // 更新异常需要返回给DSS
            throw new TException(
                "checkTokenAuthVaild faild:" + ToStringBuilder.reflectionToString(tokenInfo), e);
        }
        
    
	}

	@Override
	public String deleteDataTokenAuthVaild(TokenAuthVaild tokenInfo) throws TBusinessException, TException {
		// TODO Auto-generated method stub
		userTokenService.deleteUserToken(tokenInfo.getToken());
		return  JsonUtils.toJson(tokenInfo);
	}



}

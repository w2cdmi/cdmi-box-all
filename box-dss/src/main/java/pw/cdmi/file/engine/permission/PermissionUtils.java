/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.permission;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.job.RetryValidateTokenJob;
import pw.cdmi.file.engine.core.job.RetryValidateTokenJob.Executor;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;

/**
 * 
 * @author s90006125
 * 
 */
public final class PermissionUtils
{
    private PermissionUtils()
    {
        
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionUtils.class);
    
    private static ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private static ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    private static ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    public static void setUfmThriftClientProxyFactory(ThriftClientProxyFactory ufmThriftClientProxyFactory)
    {
        PermissionUtils.ufmThriftClientProxyFactory = ufmThriftClientProxyFactory;
    }
    
    public static void setSzufmThriftClientProxyFactory(ThriftClientProxyFactory szufmThriftClientProxyFactory)
    {
        PermissionUtils.szufmThriftClientProxyFactory = szufmThriftClientProxyFactory;
    }
    
    public static void setDgufmThriftClientProxyFactory(ThriftClientProxyFactory dgufmThriftClientProxyFactory)
    {
        PermissionUtils.dgufmThriftClientProxyFactory = dgufmThriftClientProxyFactory;
    }
    
    /**
     * 验证请求是否合法
     * 
     * @param method
     * @param objectID
     * @param token
     * @return
     */
    public static String validate(String method, String objectID, String token)
    {
        Executor executor = null;
        
        String uasRegionName = token.substring(0, SystemConfigKeys.UAS_REGION_NAME_LENGTH);
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is " + SystemConfigKeys.SZ_UAS_REGION_NAME);
                executor = new PermissionCheckExecutor(szufmThriftClientProxyFactory, method, objectID, token);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is " + SystemConfigKeys.DG_UAS_REGION_NAME);
                executor = new PermissionCheckExecutor(dgufmThriftClientProxyFactory, method, objectID, token);
                break;
            }
            default:
            {
                LOGGER.info("The checkTokenAuthVaild uas region name is default");
                executor = new PermissionCheckExecutor(ufmThriftClientProxyFactory, method, objectID, token);
                break;
            }
        }
        
        RetryValidateTokenJob job = createJob(executor);
        
        // 同步执行JOB
        if (!job.execute())
        {
            String message = "validate Token Failed [ objectID: " + objectID + ", method: " + method + " ]";
            LOGGER.warn(message);
            return null;
        }
        
        return (String) job.getResult();
        
    }
    
    private static RetryValidateTokenJob createJob(Executor executor)
    {
        return new RetryValidateTokenJob(
            SystemConfigContainer.getInteger(SystemConfigKeys.OBJECT_CALLBACK_RETRY_TIMES, 3),
            SystemConfigContainer.getLong(SystemConfigKeys.OBJECT_CALLBACK_RETRY_INTERVAL, 2000L), executor);
    }
    
    

    @SuppressWarnings("rawtypes")
    public static String getToken(HttpServletRequest request)
    {
        Object vars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (null == vars)
        {
            return null;
        }
        
        if (!(vars instanceof LinkedHashMap))
        {
            return null;
        }
        
        Object objectID = ((LinkedHashMap) vars).get(RequestConstants.REQUEST_TOKEN);
        
        if (null == objectID)
        {
            return null;
        }
        
        return (String) objectID;
    }

	public static void deleteToken(String token) {
		// TODO Auto-generated method stub
		PermissionCheckExecutor  executor = new PermissionCheckExecutor(ufmThriftClientProxyFactory, null, null, token);
        RetryValidateTokenJob job = createJob(executor);
        
        // 同步执行JOB
        if (!job.execute())
        {
            String message = "delete Token Failed [ token: " + token + "]";
            LOGGER.warn(message);
        }
	}
}

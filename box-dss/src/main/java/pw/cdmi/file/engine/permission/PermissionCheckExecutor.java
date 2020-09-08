/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.app2dc.TokenAuthVaild;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.file.engine.core.job.RetryValidateTokenJob.Executor;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;
import pw.cdmi.file.engine.manage.datacenter.thrift.OAuth2ThriftServiceClient;

/**
 * 
 * @author s90006125
 *         
 */
public class PermissionCheckExecutor implements Executor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCheckExecutor.class);
    
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private String method;
    
    private String objectId;
    
    private String token;
    
    public PermissionCheckExecutor(ThriftClientProxyFactory ufmThriftClientProxyFactory, String method,
        String objectId, String token)
    {
        this.ufmThriftClientProxyFactory = ufmThriftClientProxyFactory;
        this.method = method;
        this.objectId = objectId;
        this.token = token;
    }
    
    @SuppressWarnings("unused")
    @Override
    public String execute()
    {
        OAuth2ThriftServiceClient client = null;
        TokenAuthVaild tokenInfo = null;
        int counts = InnerLoadBalanceManager.getTryCounts();
        for (int i = 0; i < counts; i++)
        {
            try
            {
                
                client = ufmThriftClientProxyFactory.getProxy(OAuth2ThriftServiceClient.class);
                
                tokenInfo = new TokenAuthVaild(token, objectId, method);
                LOGGER.info("The checkTokenAuthVaild uas client Ip: " + client.getServerIp());
                String result="";
                if(method==null){
                	 result = client.deleteDataTokenAuthVaild(tokenInfo);
                }else{
                     result = client.checkDataTokenAuthVaild(tokenInfo);
                }
                LOGGER.debug("Result For checkToken : " + result);
                
                return result;
            }
            catch (Exception e)
            {
                LOGGER.warn("", e);
                return null;
            }
        }
        
        return null;
    }
}

package com.huawei.sharedrive.app.oauth2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 簡單授權管理類
 * 
 * @author c90006080
 * 
 */
public class Authorize
{
    private static Logger logger = LoggerFactory.getLogger(Authorize.class);
    
    private AuthorityMethod method = AuthorityMethod.GODISAGIRL;
    
    private String resource = ".*";
    
    public final static long INVAILD_ID = 0;
    
    private long resourceOwnerID = INVAILD_ID;
    
    public long getResourceOwnerID()
    {
        return resourceOwnerID;
    }
    
    public void setResourceOwnerID(long resourceOwnerID)
    {
        this.resourceOwnerID = resourceOwnerID;
    }
    
    public Authorize()
    {
    }
    
    public Authorize(AuthorityMethod method, String resource)
    {
        if (StringUtils.isNotBlank(resource))
        {
            this.resource = resource.toUpperCase(Locale.getDefault());
        }
        if (method != null)
        {
            this.method = method;
        }
    }
    
    public Authorize(AuthorityMethod method, String resource, long resourceOwnerID)
    {
        if (StringUtils.isNotBlank(resource))
        {
            this.resource = resource.toUpperCase(Locale.getDefault());
            this.resourceOwnerID = resourceOwnerID;
        }
        if (method != null)
        {
            this.method = method;
        }
    }
    
    @Override
    public String toString()
    {
        return method.name() + ':' + resource + ':' + this.resourceOwnerID;
    }
    
    public static Authorize valueOf(String authorize)
    {
        if (StringUtils.isBlank(authorize))
        {
            return new Authorize();
        }
        String[] auth = authorize.split(":");
        if (auth.length != 3)
        {
            throw new IllegalArgumentException();
        }
        AuthorityMethod method = AuthorityMethod.valueOf(auth[0].toUpperCase(Locale.getDefault()));
        String rec = auth[1].toUpperCase(Locale.getDefault());
        long resourceOwnerID = Long.parseLong(auth[2]);
        return new Authorize(method, rec, resourceOwnerID);
    }
    
    public boolean contain(Authorize authorize)
    {
        // 判断资源是否是子集
        if (!authorize.getResource().matches(resource))
        {
            logger.warn("resource not match,[{}, {}]", authorize.getResource(), resource);
            return false;
        }
        // 判断操作是否是子集
        if (!method.contain(authorize.getAuth()))
        {
            logger.warn("method not exist,[{}, {}]", method.name(), authorize.getAuth().name());
            return false;
        }
        return true;
    }
    
    public static enum AuthorityMethod
    {
        // dss 中的跨域请求
        OPTION_OBJECT,
        
        PUT_COPY, PUT_MOVE, PUT_RENAME, PUT_CREATE,
        
        // 对象整体上传下载
        PUT_OBJECT, POST_OBJECT(OPTION_OBJECT),
        
        /**
         * 分片上传，合并和列举
         */
        PUT_PART, POST_PART(OPTION_OBJECT), GET_PARTS, PUT_COMMIT, DELETE_PART,
        
        // 其他 TODO 权限需要再进一步整理
        /**
         * 获取缩略图
         */
        GET_METADATA, GET_THUMBNAIL, GET_PREVIEW, GET_INFO,
        
        /**
         * 整体下载
         */
        GET_OBJECT(GET_THUMBNAIL, GET_PREVIEW, GET_INFO),
        
        GET_ALL(GET_OBJECT, GET_THUMBNAIL, GET_PREVIEW,GET_INFO, GET_METADATA,PUT_COPY),
        
        // 上传对象（统指上传业务所有操作）
        UPLOAD_OBJECT(PUT_OBJECT, POST_OBJECT, PUT_PART, POST_PART, GET_PARTS, PUT_COMMIT, DELETE_PART, OPTION_OBJECT, PUT_CREATE),
        
        PUT_ALL(PUT_COPY, PUT_MOVE, PUT_RENAME, PUT_CREATE, PUT_OBJECT, POST_OBJECT),
        
        DELETE_ALL,
        
        // 消息通知监听
        MESSAGE_LISTEN,
        
        /**
         * 统指所有操作
         */
        GODISAGIRL;
        
        private List<AuthorityMethod> subAuthList = new ArrayList<AuthorityMethod>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        private AuthorityMethod(AuthorityMethod... authorities)
        {
            for (AuthorityMethod auth : authorities)
            {
                subAuthList.add(auth);
            }
            
        }
        
        public boolean contain(AuthorityMethod auth)
        {
            if (auth == null)
            {
                return false;
            }
            if (auth.equals(this) || GODISAGIRL.equals(this))
            {
                return true;
            }
            for (AuthorityMethod subAuth : subAuthList)
            {
                if (auth.equals(subAuth))
                {
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * @return the auth
     */
    public AuthorityMethod getAuth()
    {
        return method;
    }
    
    /**
     * @return the resource
     */
    public String getResource()
    {
        return resource;
    }
}

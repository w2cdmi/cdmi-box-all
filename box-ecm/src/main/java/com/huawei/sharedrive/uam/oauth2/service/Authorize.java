package com.huawei.sharedrive.uam.oauth2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class Authorize
{
    private static final int INITIAL_CAPACITIES = 10;
    
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
        if (!authorize.getResource().matches(resource))
        {
            return false;
        }
        if (!method.contain(authorize.getAuth()))
        {
            return false;
        }
        return true;
    }
    
    public static enum AuthorityMethod
    {
        PUT_OBJECT, POST_OBJECT,
        
        PUT_PART, POST_PART, GET_PARTS, PUT_COMMIT, DELETE_PART,
        
        GET_METADATA, GET_THUMBNAIL, GET_PREVIEW, GET_INFO,
        
        GET_OBJECT(GET_THUMBNAIL, GET_PREVIEW, GET_INFO), GET_ALL(GET_OBJECT, GET_THUMBNAIL, GET_PREVIEW,
            GET_INFO, GET_METADATA),
        
        UPLOAD_OBJECT(PUT_OBJECT, POST_OBJECT, PUT_PART, POST_PART, GET_PARTS, PUT_COMMIT, DELETE_PART),
        
        PUT_COPY, PUT_MOVE, PUT_RENAME,
        
        PUT_ALL(PUT_COPY, PUT_MOVE, PUT_RENAME, PUT_OBJECT, POST_OBJECT),
        
        DELETE_ALL,
        
        GODISAGIRL;
        
        private List<AuthorityMethod> subAuthList = new ArrayList<AuthorityMethod>(INITIAL_CAPACITIES);
        
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

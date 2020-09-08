package com.huawei.sharedrive.app.test.openapi.favorite;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class FavoriteBaseTest
{
    protected static final String METHOD_DELETE = "DELETE";
    
    protected static final String METHOD_GET = "GET";
    
    protected static final String METHOD_POST = "POST";
    
    protected static final String METHOD_PUT = "PUT";
    
    protected Long fileParentId;
    
    protected long userId1;
    
    protected long userId2;
    
    protected boolean showResult = true;
    
    protected String url;
    
    public FavoriteBaseTest()
    {
        try
        {
            userId1 = MyTestUtils.getTestUser1().getCloudUserId();
            userId2 = MyTestUtils.getTestUser2().getCloudUserId();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Get owner id failed!");
        }
    }
    
    protected HttpURLConnection getConnection(String requestUrl, String method, String authorization,
        String body) throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", authorization);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        if (StringUtils.isNotEmpty(body))
        {
           connection.setRequestProperty("Content-type", "application/json");
            // connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
            
        }
        return connection;
    }
    
    public class RequestNode
    {
        
        private Byte type;
        
        private Long ownerId;
        
        private Long nodeId;
        
        private String linkcode;
        
        private String name;
        
        private Long parentId;
        
        public Byte getType()
        {
            return type;
        }
        
        public void setType(Byte type)
        {
            this.type = type;
        }
        
        public Long getOwnerId()
        {
            return ownerId;
        }
        
        public void setOwnerId(Long ownerId)
        {
            this.ownerId = ownerId;
        }
        
        public Long getNodeId()
        {
            return nodeId;
        }
        
        public void setNodeId(Long nodeId)
        {
            this.nodeId = nodeId;
        }
        
        public String getLinkcode()
        {
            return linkcode;
        }
        
        public void setLinkcode(String linkcode)
        {
            this.linkcode = linkcode;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public Long getParentId()
        {
            return parentId;
        }
        
        public void setParentId(Long parentId)
        {
            this.parentId = parentId;
        }
        
    }
}

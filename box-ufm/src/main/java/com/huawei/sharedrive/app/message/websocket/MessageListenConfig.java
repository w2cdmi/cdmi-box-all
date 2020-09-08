package com.huawei.sharedrive.app.message.websocket;

import java.util.List;

import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;

import pw.cdmi.core.utils.SpringContextUtil;

import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;

public class MessageListenConfig extends ServerEndpointConfig.Configurator
{
    public static final String HEADER_SOCKET_ID = "X-Socket-Id";
    
    @Override
    public boolean checkOrigin(String originHeaderValue)
    {
        return super.checkOrigin(originHeaderValue);
    }
    
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException
    {
        return super.getEndpointInstance(clazz);
    }
    
    @Override
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested)
    {
        return super.getNegotiatedExtensions(installed, requested);
    }
    
    @Override
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested)
    {
        return super.getNegotiatedSubprotocol(supported, requested);
    }
    
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response)
    {
        // token鉴权
        String uri = request.getRequestURI().getPath();
        int lastIndex = uri.lastIndexOf('/');
        String receiverId = uri.substring(0, lastIndex);
        receiverId = receiverId.substring(receiverId.lastIndexOf('/') + 1);
        
        String token = uri.substring(lastIndex + 1);
        UserTokenHelper userTokenHelper = (UserTokenHelper) SpringContextUtil.getBean("userTokenHelper");
        userTokenHelper.checkMessageListenToken(token, receiverId, AuthorityMethod.MESSAGE_LISTEN);
        super.modifyHandshake(sec, request, response);
    }
    
}

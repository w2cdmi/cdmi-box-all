package com.huawei.sharedrive.uam.security.domain;

import org.springframework.beans.BeanUtils;

import com.huawei.sharedrive.uam.httpclient.rest.response.INodeResponse;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.user.domain.User;

public class SecurityPrincipal extends UserToken
{
    
    private static final long serialVersionUID = -8366455569071006965L;
    
    private User onwerUser;
    
    private INodeResponse iNode;
    
    public INodeResponse getiNode()
    {
        return iNode;
    }
    
    public void setiNode(INodeResponse iNode)
    {
        this.iNode = iNode;
    }
    
    public User getOnwerUser()
    {
        return onwerUser;
    }
    
    public void setOnwerUser(User onwerUser)
    {
        this.onwerUser = onwerUser;
    }
    
    public SecurityPrincipal(UserToken userToken)
    {
        BeanUtils.copyProperties(userToken, this);
    }
}

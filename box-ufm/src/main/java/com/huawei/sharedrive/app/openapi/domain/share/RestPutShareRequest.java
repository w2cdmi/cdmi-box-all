package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.share.domain.INodeShare;

/**
 * 添加共享请求
 * @author l90003768
 *
 */
public class RestPutShareRequest
{
    public static final int MAX_MESSAGE_LENGTH = 5000;
    
    private String message;
    
    private List<INodeShare> shareList;

    public String getMessage()
    {
        return message;
    }

    public List<INodeShare> getShareList()
    {
        return shareList;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setShareList(List<INodeShare> shareList)
    {
        this.shareList = shareList;
    }
    
    public void checkParameter()
    {
        if (message != null && message.length() > MAX_MESSAGE_LENGTH)
        {
            throw new InvalidParamException("Message length invalid");
        }
    }
    
}

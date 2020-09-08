package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.MailMsg;

public class RestMailMsgSetRequest
{
    public static final int MAX_MAIL_SUBJECT_LENGTH = 255;
    
    public static final int MAX_MAIL_MSG_LENGTH = 10000;
    
    private String source;
    
    private String subject;
    
    private String message;

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public void checkParameter() throws BaseRunException
    {
        if (source == null)
        {
            throw new InvalidParamException("type is null");
        }
        
        // 校验subject合法性
        if (subject != null && subject.length() > MAX_MAIL_SUBJECT_LENGTH)
        {
            throw new InvalidParamException("Invalid subject: " + subject);
        }
        
        if(!MailMsg.SOURCE_LINK.equals(source) && !MailMsg.SOURCE_SHARE.equals(source))
        {
            throw new InvalidParamException("source is invalid:" + source);
        }
        
        if (message == null)
        {
            throw new InvalidParamException("message is null");
        }
        
        if (message.length() > MAX_MAIL_MSG_LENGTH)
        {
            throw new InvalidParamException("Invalid message: " + message);
        }
    }
}

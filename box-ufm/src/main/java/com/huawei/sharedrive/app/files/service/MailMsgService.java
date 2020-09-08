package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.files.domain.MailMsg;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface MailMsgService
{
    /**
     * @param msg
     */
    MailMsg createMailMsg(UserToken user, MailMsg msg);
    
    /**
     * @param msg
     * @return
     */
    MailMsg updateMailMsg(UserToken user, MailMsg msg);
    
    /**
     * @param msg
     * @return
     */
    MailMsg getMailMsg(UserToken user, String source, long ownerId, long nodeId);
}

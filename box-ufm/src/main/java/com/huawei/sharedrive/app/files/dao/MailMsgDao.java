package com.huawei.sharedrive.app.files.dao;

import com.huawei.sharedrive.app.files.domain.MailMsg;

public interface MailMsgDao
{
    /**
     * @param msg
     */
    void create(MailMsg msg);
    
    /**
     * @param msg
     * @return
     */
    int delete(MailMsg msg);
    
    /**
     * @param msg
     * @return
     */
    int deleteUserAll(long userId);
    
    /**
     * @param msg
     * @return
     */
    int update(MailMsg msg);
    
    /**
     * @param msg
     * @return
     */
    MailMsg get(MailMsg msg);
}

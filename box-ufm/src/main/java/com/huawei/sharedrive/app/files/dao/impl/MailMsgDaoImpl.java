package com.huawei.sharedrive.app.files.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.MailMsgDao;
import com.huawei.sharedrive.app.files.domain.MailMsg;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("mailMsgDao")
@SuppressWarnings("deprecation")
public class MailMsgDaoImpl extends AbstractDAOImpl implements MailMsgDao
{
    @Override
    public void create(MailMsg msg)
    {
        sqlMapClientTemplate.insert("MailMsg.insert", msg);
    }
    
    @Override
    public int delete(MailMsg msg)
    {
        return sqlMapClientTemplate.delete("MailMsg.delete", msg);
    }
    
    @Override
    public MailMsg get(MailMsg msg)
    {
        return (MailMsg) sqlMapClientTemplate.queryForObject("MailMsg.get", msg);
    }

    @Override
    public int deleteUserAll(long userId)
    {
        MailMsg msg = new MailMsg();
        msg.setUserId(userId);
        return sqlMapClientTemplate.delete("MailMsg.deleteUserAll", msg);
    }

    @Override
    public int update(MailMsg msg)
    {
        return sqlMapClientTemplate.update("MailMsg.update", msg);
    }
    
}

package com.huawei.sharedrive.uam.user.dao.impl;

import com.huawei.sharedrive.uam.user.dao.MessageTemplateDao;
import com.huawei.sharedrive.uam.user.domain.MessageTemplate;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("messageTemplateDao")
public class MessageTemplateDaoImpl extends AbstractDAOImpl implements MessageTemplateDao {
    @Override
    public MessageTemplate getById(String id) {
        return (MessageTemplate) sqlMapClientTemplate.queryForObject("MessageTemplate.getById", id);
    }

    @Override
    public void create(MessageTemplate template) {
        sqlMapClientTemplate.insert("MessageTemplate.insert", template);
    }

    @Override
    public void update(MessageTemplate template) {
        sqlMapClientTemplate.update("MessageTemplate.update", template);
    }

    @Override
    public void delete(String id) {
        sqlMapClientTemplate.delete("MessageTemplate.delete", id);
    }
}

package com.huawei.sharedrive.uam.user.dao;


import com.huawei.sharedrive.uam.user.domain.MessageTemplate;

public interface MessageTemplateDao {
    MessageTemplate getById(String id);

    void create(MessageTemplate template);

    void update(MessageTemplate template);

    void delete(String id);
}

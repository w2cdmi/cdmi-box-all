package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.user.domain.MessageTemplate;

public interface MessageTemplateService {
    /**
     */
    MessageTemplate getById(String id);

    void create(MessageTemplate template);

    void update(MessageTemplate template);

    void delete(String id);
}

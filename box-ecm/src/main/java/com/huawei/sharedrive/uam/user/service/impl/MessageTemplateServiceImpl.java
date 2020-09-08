
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.user.service.impl;

import com.huawei.sharedrive.uam.user.dao.MessageTemplateDao;
import com.huawei.sharedrive.uam.user.domain.MessageTemplate;
import com.huawei.sharedrive.uam.user.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/************************************************************
 * @Description:
 * <pre>消息模板</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/12/29
 ************************************************************/
@Component
public class MessageTemplateServiceImpl implements MessageTemplateService {
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public MessageTemplate getById(String id) {
        return messageTemplateDao.getById(id);
    }

    @Override
    public void create(MessageTemplate template) {
        messageTemplateDao.create(template);
    }

    @Override
    public void update(MessageTemplate template) {
        messageTemplateDao.update(template);
    }

    @Override
    public void delete(String id) {
        messageTemplateDao.delete(id);
    }
}

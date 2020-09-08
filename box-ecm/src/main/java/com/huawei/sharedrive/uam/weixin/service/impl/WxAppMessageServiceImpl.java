package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.*;
import com.huawei.sharedrive.uam.weixin.service.WwAppMessageService;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.deamon.DeamonService;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>第三方应用安装后，用户修改该应用的配置（如果通讯录范围）时，相关事件会通过AppEvent进行通知</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxAppMessageServiceImpl implements WwAppMessageService {
    @Autowired
    WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxEnterpriseUserService wxEnterpriseUserService;

    @Autowired
    WxDepartmentService wxDepartmentService;

    @Autowired
    DeamonService deamonService;

    @Override
    public void handle(WwAppMessage m) {
        if(m instanceof WwAppTextMessage) {
            onAppTextMessage((WwAppTextMessage) m);
        } else if(m instanceof WwAppImageMessage) {
            onAppImageMessage((WwAppImageMessage) m);
        } else if(m instanceof WwAppVoiceMessage) {
            onAppVoiceMessage((WwAppVoiceMessage) m);
        } else if(m instanceof WwAppVideoMessage) {
            onAppVideoMessage((WwAppVideoMessage) m);
        } else if(m instanceof WwAppLocationMessage) {
            onAppLocationMessage((WwAppLocationMessage) m);
        } else if(m instanceof WwAppLinkMessage) {
            onAppLinkMessage((WwAppLinkMessage) m);
        }
    }

    void onAppTextMessage(WwAppTextMessage e) {
/*
        //放入后台执行
        AppSubscribeTask task = SpringContextUtil.getBean(AppSubscribeTask.class);
        task.setEvent(e);

        deamonService.execute(task);
*/
    }

    void onAppImageMessage(WwAppImageMessage e) {
/*
        //放入后台执行
        AppUnsubscribeTask task = SpringContextUtil.getBean(AppUnsubscribeTask.class);
        task.setEvent(e);

        deamonService.execute(task);
*/
    }

    void onAppVoiceMessage(WwAppVoiceMessage e) {

    }

    void onAppVideoMessage(WwAppVideoMessage e) {

    }

    void onAppLocationMessage(WwAppLocationMessage e) {

    }

    void onAppLinkMessage(WwAppLinkMessage e) {
/*
        WxEnterpriseUser user = WxDomainUtils.toWxEnterpriseUser(e);
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        user.setBoxEnterpriseId(wxEnterprise.getBoxEnterpriseId());
        wxEnterpriseUserService.create(user);
*/
    }
}

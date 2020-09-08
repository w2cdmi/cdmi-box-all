package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.*;
import com.huawei.sharedrive.uam.weixin.service.WxAppEventService;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseUserService;
import com.huawei.sharedrive.uam.weixin.service.task.AppSubscribeTask;
import com.huawei.sharedrive.uam.weixin.service.task.AppUnsubscribeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.deamon.DeamonService;
import pw.cdmi.core.utils.SpringContextUtil;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>第三方应用安装后，用户修改该应用的配置（如果通讯录范围）时，相关事件会通过AppEvent进行通知</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxAppEventServiceImpl implements WxAppEventService {
    @Autowired
    WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxEnterpriseUserService wxEnterpriseUserService;

    @Autowired
    WxDepartmentService wxDepartmentService;

    @Autowired
    DeamonService deamonService;

    private String corpId = "wwba09b5d7931f8d7e";

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    @Override
    public void handle(WxAppEvent e) {
        if(e instanceof AppSubscribeEvent) {
            onAppSubscribe((AppSubscribeEvent) e);
        } else if(e instanceof AppUnsubscribeEvent) {
            onAppUnsubscribe((AppUnsubscribeEvent) e);
        } else if(e instanceof AppEnterAgentEvent) {
            onAppEnterAgent((AppEnterAgentEvent) e);
        } else if(e instanceof AppLocationEvent) {
            onAppLocation((AppLocationEvent) e);
        } else if(e instanceof AppBatchJobResultEvent) {
            onAppBatchJobResult((AppBatchJobResultEvent) e);
        } else if(e instanceof AppCreateUserEvent) {
            onAppCreateUser((AppCreateUserEvent) e);
        } else if(e instanceof AppUpdateUserEvent) {
            onAppUpdateUser((AppUpdateUserEvent) e);
        } else if(e instanceof AppDeleteUserEvent) {
            onAppDeleteUser((AppDeleteUserEvent) e);
        } else if(e instanceof AppCreatePartyEvent) {
            onAppCreateParty((AppCreatePartyEvent) e);
        } else if(e instanceof AppUpdatePartyEvent) {
            onAppUpdateParty((AppUpdatePartyEvent) e);
        } else if(e instanceof AppDeletePartyEvent) {
            onAppDeleteParty((AppDeletePartyEvent) e);
        } else if(e instanceof AppUpdateTagEvent) {
            onAppUpdateTag((AppUpdateTagEvent) e);
        } else if(e instanceof AppClickEvent) {
            onAppClick((AppClickEvent) e);
        } else if(e instanceof AppViewEvent) {
            onAppView((AppViewEvent) e);
        } else if(e instanceof AppScanCodePushEvent) {
            onAppScanCodePush((AppScanCodePushEvent) e);
        } else if(e instanceof AppScanCodeWaitMessageEvent) {
            onAppScanCodeWaitMessage((AppScanCodeWaitMessageEvent) e);
        } else if(e instanceof AppPicturePhoneEvent) {
            onAppPicturePhone((AppPicturePhoneEvent) e);
        } else if(e instanceof AppPicturePhoneOrAlbumEvent) {
            onAppPicturePhoneOrAlbum((AppPicturePhoneOrAlbumEvent) e);
        } else if(e instanceof AppPictureWeixinEvent) {
            onAppPictureWeixin((AppPictureWeixinEvent) e);
        } else if(e instanceof AppLocationSelectEvent) {
            onAppLocationSelect((AppLocationSelectEvent) e);
        }
    }

    void onAppSubscribe(AppSubscribeEvent e) {
        //放入后台执行
        AppSubscribeTask task = SpringContextUtil.getBean(AppSubscribeTask.class);
        task.setEvent(e);

        deamonService.execute(task);
    }

    void onAppUnsubscribe(AppUnsubscribeEvent e) {
        //放入后台执行
        AppUnsubscribeTask task = SpringContextUtil.getBean(AppUnsubscribeTask.class);
        task.setEvent(e);

        deamonService.execute(task);
    }

    void onAppEnterAgent(AppEnterAgentEvent e) {

    }

    void onAppLocation(AppLocationEvent e) {

    }

    void onAppBatchJobResult(AppBatchJobResultEvent e) {

    }

    void onAppCreateUser(AppCreateUserEvent e) {
/*
        WxEnterpriseUser user = WxDomainUtils.toWxEnterpriseUser(e);
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        user.setBoxEnterpriseId(wxEnterprise.getBoxEnterpriseId());
        wxEnterpriseUserService.create(user);
*/
    }

    void onAppUpdateUser(AppUpdateUserEvent e) {
/*
        WxEnterpriseUser user = WxDomainUtils.toWxEnterpriseUser(e);
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        user.setBoxEnterpriseId(wxEnterprise.getBoxEnterpriseId());
        wxEnterpriseUserService.update(user);
*/
    }

    void onAppDeleteUser(AppDeleteUserEvent e) {
/*
        wxEnterpriseUserService.delete(corpId, e.getUserID());
*/
    }

    void onAppCreateParty(AppCreatePartyEvent e) {
/*
        WxDepartment dept = WxDomainUtils.toWxDepartment(e);
        dept.setCorpId(corpId);
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        dept.setBoxEnterpriseId(wxEnterprise.getBoxEnterpriseId());
        wxDepartmentService.create(dept);
*/
    }

    void onAppUpdateParty(AppUpdatePartyEvent e) {
/*
        WxDepartment dept = WxDomainUtils.toWxDepartment(e);
        dept.setCorpId(corpId);
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        dept.setBoxEnterpriseId(wxEnterprise.getBoxEnterpriseId());
        wxDepartmentService.update(dept);
*/
    }

    void onAppDeleteParty(AppDeletePartyEvent e) {
/*
        wxDepartmentService.delete(corpId, e.getId());
*/
    }

    void onAppUpdateTag(AppUpdateTagEvent e) {

    }

    void onAppClick(AppClickEvent e) {

    }

    void onAppView(AppViewEvent e) {

    }

    void onAppScanCodePush(AppScanCodePushEvent e) {

    }

    void onAppScanCodeWaitMessage(AppScanCodeWaitMessageEvent e) {

    }

    void onAppPicturePhone(AppPicturePhoneEvent e) {

    }

    void onAppPicturePhoneOrAlbum(AppPicturePhoneOrAlbumEvent e) {

    }

    void onAppPictureWeixin(AppPictureWeixinEvent e) {

    }

    void onAppLocationSelect(AppLocationSelectEvent e) {

    }
}

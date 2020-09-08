/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountRequest;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import pw.cdmi.common.domain.enterprise.Enterprise;

import java.io.IOException;
import java.util.Locale;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class SyncEnterpriseAccountTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SyncEnterpriseAccountTask.class);

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    @Autowired
    WxWorkCorpAppService wxWorkCorpAppService;

    @Autowired
    private EnterpriseBySelfService enterpriseBySelfService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private SystemProfileService systemProfileService;

    private WxEnterprise wxEnterprise;

    public SyncEnterpriseAccountTask() {
    }

    public WxEnterprise getWxEnterprise() {
        return wxEnterprise;
    }

    public void setWxEnterprise(WxEnterprise wxEnterprise) {
        this.wxEnterprise = wxEnterprise;
    }

    @Override
    public void run() {
        //企业信息
        if(wxEnterprise.getBoxEnterpriseId() == null) {
            registerEnterprise(wxEnterprise);
        } else {
            Enterprise enterprise = enterpriseService.getById(wxEnterprise.getBoxEnterpriseId());
            if(enterprise == null) {
                registerEnterprise(wxEnterprise);
            } else {
                logger.info("The enterprise account has existed, only update the enterprise status, corpId={}, enterpriseId={}", wxEnterprise.getId(), enterprise.getId());
                updateEnterprise(enterprise);
            }
        }
    }

    private void registerEnterprise(WxEnterprise wxEnterprise) {
        try {
            //企业账号开户
            Enterprise enterprise = createEnterprise(wxEnterprise);

            //开户成功，设置内部的
            wxEnterprise.setState(WxEnterprise.STATE_NORMAL); //设置为完成
            wxEnterprise.setBoxEnterpriseId(enterprise.getId());
            wxEnterpriseService.update(wxEnterprise);
        } catch (Exception e) {
            logger.error("Failed to register enterprise: corpId={}, error={}", wxEnterprise.getId(), e.getMessage());
            logger.error("Failed to register enterprise:", e);
        }
    }

    protected Enterprise createEnterprise(WxEnterprise wxEnterprise) throws IOException {
        //使用原有自注册流程
        Enterprise enterprise = new Enterprise();
        enterprise.setContactEmail(wxEnterprise.getEmail());
        enterprise.setContactPerson(wxEnterprise.getUserId());
        enterprise.setContactPhone(wxEnterprise.getMobile());
        enterprise.setDomainName(wxEnterprise.getId());
        enterprise.setName(wxEnterprise.getName());
        enterprise.setIsdepartment(true);
        enterprise.setPwdLevel("3");//默认初级限制，只要求不少于6位

        RestEnterpriseAccountRequest accountRequest = new RestEnterpriseAccountRequest();
        //设置配额
        buildAccountParameter(accountRequest);

        //注册时，会为企业生成一个对应的enterpriseUser, 其name使用account赋值
        String accountId = getAccountId(wxEnterprise);

        //原有的流程中使用了HttpServletRequest参数（获取IP和Locale）, 为了使用既有流程此处构造一个Request。
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "qyapi.weixin.qq.com");
        request.addPreferredLocale(Locale.SIMPLIFIED_CHINESE);

        enterpriseBySelfService.enterpriseRegister(enterprise, accountRequest, accountId,accountId, request);
        return enterprise;
    }

    private void updateEnterprise(Enterprise enterprise) {
        if(enterprise.getStatus() != Enterprise.STATUS_ENABLE) {
            enterprise.setStatus(Enterprise.STATUS_ENABLE);

            enterpriseService.updateStatus(enterprise);
        }
    }

    private void buildAccountParameter(RestEnterpriseAccountRequest request) {
        String appId = authAppService.getDefaultWebApp().getAuthAppId();
        EnterpriseAccountProfile profile = systemProfileService.buildEnterpriseAccountProfile(appId);

        request.setMaxMember(profile.getMaxUserAmount());
        request.setMaxSpace(profile.getMaxTeamspaceQuota());
        request.setMaxTeamspace(profile.getMaxTeamspaceAmount());
    }

    protected String getAccountId(WxEnterprise wxEnterprise) {

        //如果以上所有的信息都为空，使用企业的ID作为账号。
        return wxEnterprise.getId();
    }
}

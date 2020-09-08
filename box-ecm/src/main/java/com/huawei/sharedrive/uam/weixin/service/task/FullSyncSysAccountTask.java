/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.AuthInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.SpringContextUtil;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class FullSyncSysAccountTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(FullSyncSysAccountTask.class);

    @Autowired
    WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    @Autowired
    WxWorkCorpAppService wxWorkCorpAppService;

    private String suiteId;

    private WxEnterprise wxEnterprise;

    private AuthInfo authInfo;

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public WxEnterprise getWxEnterprise() {
        return wxEnterprise;
    }

    public void setWxEnterprise(WxEnterprise wxEnterprise) {
        this.wxEnterprise = wxEnterprise;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    @Override
    public void run() {
        try {
            //启动企业信息同步
            SyncEnterpriseAccountTask enterpriseTask = SpringContextUtil.getBean(SyncEnterpriseAccountTask.class);
            enterpriseTask.setWxEnterprise(wxEnterprise);

            enterpriseTask.run();

            //同步通信录任务
            SyncAddressListTask addressTask = SpringContextUtil.getBean(SyncAddressListTask.class);
            addressTask.setWxEnterprise(wxEnterprise);
            addressTask.setAuthInfo(authInfo);

            addressTask.run();
        } catch (Exception e) {
            logger.error("Failed to synchronize WxEnterprise: corpId={}, error={}", wxEnterprise.getId(), e.getMessage());
            logger.error("Failed to synchronize WxEnterprise: ", e);
        }
    }
}

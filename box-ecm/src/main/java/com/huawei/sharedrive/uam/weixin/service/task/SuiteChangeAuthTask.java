/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.event.SuiteChangeAuthEvent;
import com.huawei.sharedrive.uam.weixin.rest.CorpAuthInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;
import org.apache.commons.lang.StringUtils;
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
public class SuiteChangeAuthTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteChangeAuthTask.class);

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxWorkOauth2Service wxWorkOauth2Service;

    @Autowired
    WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    @Autowired
    WxWorkCorpAppService wxWorkCorpAppService;

    public SuiteChangeAuthTask() {
    }

    @Override
    public void run() {
        SuiteChangeAuthEvent authEvent = (SuiteChangeAuthEvent)event;
        String corpId = authEvent.getAuthCorpId();
        String suiteId = authEvent.getSuiteId();

        try {
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            if(wxEnterprise == null) {
                logger.error("Failed to handle change_auth event: corpId={}, error=No such WxEnterprise exists.", corpId);
                return;
            }

            String code = wxEnterprise.getPermanentCode();
            if(StringUtils.isBlank(code)) {
                logger.error("Failed to handle change_auth event: corpId={}, error=Can't get permanent code of corporation.", corpId);
                return;
            }

            CorpAuthInfo corpAuthInfo = wxWorkOauth2Service.getCorpAuthInfo(corpId, code);
            if(corpAuthInfo == null) {
                logger.error("Failed to handle change_auth event: corpId={}, error=Get null CorpAuthInfo.", corpId);
                return;
            }

            if(corpAuthInfo.hasError()) {
                logger.error("Failed to handle change_auth event, error occurred while getting CorpAuthInfo of corporation: corpId={}, errcode={}, errmsg={}", corpId, corpAuthInfo.getErrcode(), corpAuthInfo.getErrmsg());
                return;
            }

            //企业账号全同步
            FullSyncSysAccountTask syncTask = SpringContextUtil.getBean(FullSyncSysAccountTask.class);
            syncTask.setSuiteId(suiteId);
            syncTask.setWxEnterprise(wxEnterprise);
            syncTask.setAuthInfo(corpAuthInfo.getAuthInfo());

            syncTask.run();
        } catch (Exception e) {
            logger.error("Failed to handle change_auth event: corpId={}, error={}", corpId, e.getMessage());
            logger.error("Failed to handle change_auth event: ", e);
        }
    }
}

/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.event.SuiteChangeAuthEvent;
import com.huawei.sharedrive.uam.weixin.event.SuiteUpdateUserEvent;
import com.huawei.sharedrive.uam.weixin.rest.User;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkUserManager;
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
public class SuiteUpdateUserTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteUpdateUserTask.class);

    @Autowired
    private WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    @Autowired
    private WxWorkUserManager wxWorkUserManager;

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    public SuiteUpdateUserTask() {
    }

    @Override
    public void run() {
        SuiteUpdateUserEvent updateEvent = (SuiteUpdateUserEvent)event;
        String suiteId = updateEvent.getSuiteId();
        String corpId = updateEvent.getAuthCorpId();
        String userId = updateEvent.getUserID();

        try {
            String newUserId = updateEvent.getNewUserID();
            //Update事件中只传递了修改过的字段，为了简便起见，重新获取用户信息，进行更新。
            String wwUserId = userId;
            if(StringUtils.isNotBlank(newUserId)) {
                //不为空，说明用户修改了userId. 需要使用新的userId来获取用户信息。
                wwUserId = newUserId;
            }

            User user = wxWorkOauth2SuiteProxy.getUserInfo(corpId, wwUserId);
            if(user == null) {
                logger.error("Failed to query user info: corpId={}, userId={}", corpId, userId);
                return;
            }

            if(user.hasError()) {
                logger.error("Failed to query user info: corpId={}, userId={}, code={}, msg={}", corpId, userId, user.getErrcode(), user.getErrmsg());
                return;
            }

            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            if(wxEnterprise == null) {
                logger.error("Failed to update party, no WxEnterprise found in database. corpId={}", corpId);
                return;
            }

            if(wxEnterprise.getBoxEnterpriseId() == null) {
                logger.warn("No Enterprise found in database, just open the enterprise account. corpId={}", corpId);

                //生成企业账号，走一遍同步流程。同步后用户信息已经是最新的，不再需要更新。
                SuiteChangeAuthTask task = SpringContextUtil.getBean(SuiteChangeAuthTask.class);
                task.setEvent(new SuiteChangeAuthEvent(suiteId, corpId));
                task.run();

                return;
            }

            long enterpriseId = wxEnterprise.getBoxEnterpriseId();
            EnterpriseUser sysUser = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, userId);
            if(sysUser != null) {
                wxWorkUserManager.updateAccount(enterpriseId, user);
            } else {
                //异常情况：用户不存在
                wxWorkUserManager.openAccount(enterpriseId, user);
            }
        } catch (Exception e) {
            logger.error("Failed to open account: corpId={}, userId={}", corpId, userId);
            logger.error("Failed to open account:", e);
//            e.printStackTrace();
        }
    }
}



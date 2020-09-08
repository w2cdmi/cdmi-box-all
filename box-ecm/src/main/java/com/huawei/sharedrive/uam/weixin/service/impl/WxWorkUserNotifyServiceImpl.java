/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.user.service.UserNotifyService;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseUserService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/************************************************************
 * @Description:
 * <pre>给企业微信发送消息</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/12/6
 ************************************************************/
@Service
public class WxWorkUserNotifyServiceImpl implements UserNotifyService {
    private static final Logger logger = LoggerFactory.getLogger(WxEnterpriseUserServiceImpl.class);

    @Autowired
    private WxEnterpriseUserService wxEnterpriseUserService;

    @Autowired
    private WxWorkCorpAppService wxWorkCorpAppService;

    @Autowired
    WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    @Override
    public void sendMessage(long enterpriseId, long enterpriseUserId, String message) {
        List<WxEnterpriseUser> userList = wxEnterpriseUserService.getByEnterpriseIdAndUserId(enterpriseId, enterpriseUserId);
        //正常情况下，一个内部账号只会绑定到一个企业微信账号中。
        if(userList.size() > 1) {
            logger.warn("found more than one WxWork account bound to a EnterpriseUser: enterpriseId={}, userId={}", enterpriseId, enterpriseUserId);
        }

        for(WxEnterpriseUser user : userList) {
            //通过企业微信发送通知消息
            List<WxWorkCorpApp> appList = wxWorkCorpAppService.getByCorpId(user.getCorpId());
            if (appList != null && !appList.isEmpty()) {
                for (WxWorkCorpApp app : appList) {
                    wxWorkOauth2SuiteProxy.sendTextMessage(app.getCorpId(), app.getAgentId(), user.getUserId(), message);
                }
            }
        }
    }
}

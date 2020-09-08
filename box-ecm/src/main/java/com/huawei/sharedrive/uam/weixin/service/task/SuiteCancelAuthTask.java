/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.event.SuiteCancelAuthEvent;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class SuiteCancelAuthTask extends SuiteTask {
    private static Logger logger = LoggerFactory.getLogger(SuiteCancelAuthTask.class);

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    private WxWorkCorpAppService wxWorkCorpAppService;

    public SuiteCancelAuthTask() {
    }

    @Override
    public void run() {
        SuiteCancelAuthEvent authEvent = (SuiteCancelAuthEvent)event;
        String corpId = authEvent.getAuthCorpId();
        String suiteId = authEvent.getSuiteId();

        //删除企业的应用安装信息
        wxWorkCorpAppService.deleteByCorpIdAndSuiteId(corpId, suiteId);

        List<WxWorkCorpApp> appList = wxWorkCorpAppService.getByCorpId(corpId);

        //如果已经没有安装的应用，执行销户操作
        if(appList.isEmpty()) {
            //销户只更新状态，不删除数据。
            //TODO: 重新检查企业销户流程
            wxEnterpriseService.updateState(corpId, WxEnterprise.STATE_CLOSED);
        }
    }
}

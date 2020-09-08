/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.rest.Agent;
import com.huawei.sharedrive.uam.weixin.rest.PermanentCodeInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import com.huawei.sharedrive.uam.weixin.service.impl.WxDomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.SpringContextUtil;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业管理员授权后，为企业开户，并初始化部门和用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Component
@Scope("prototype")
public class AppSubscribeTask extends AppTask {
    private static Logger logger = LoggerFactory.getLogger(AppSubscribeTask.class);

    public AppSubscribeTask() {
    }

    @Override
    public void run() {
    }
}

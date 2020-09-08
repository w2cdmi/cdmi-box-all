package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.event.SysRegisterCorpEvent;
import com.huawei.sharedrive.uam.weixin.event.WxSysEvent;
import com.huawei.sharedrive.uam.weixin.service.*;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WeixinEventService实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxSysEventServiceImpl implements WxSysEventService {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkOauth2SuiteServiceImpl.class);

    @Override
    public void handle(WxSysEvent e) {
        if(e instanceof SysRegisterCorpEvent) {
            onSysRegisterCorp((SysRegisterCorpEvent) e);
        }
    }

    //企业注册事件
    void onSysRegisterCorp(SysRegisterCorpEvent e) {
//        String ticket = e.getAuthCorpId();
    }
}

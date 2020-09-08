package com.huawei.sharedrive.uam.weixin.service;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.WwAppMessage;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>处理用户发送的应用消息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public interface WwAppMessageService {
    /**
     * 处理用户发送的应用消息
     * @param message 应用消息
     */
    void handle(WwAppMessage message);
}

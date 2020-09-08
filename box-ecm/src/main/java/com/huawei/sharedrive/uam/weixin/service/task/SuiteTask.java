/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.task;

import com.huawei.sharedrive.uam.weixin.event.WxSuiteEvent;
import org.springframework.context.annotation.Scope;

/************************************************************
 * @Description:
 * <pre>Suite任务基类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/25
 ************************************************************/
@Scope("prototype")
public abstract class SuiteTask implements Runnable{
    protected WxSuiteEvent event;

    public WxSuiteEvent getEvent() {
        return event;
    }

    public void setEvent(WxSuiteEvent event) {
        this.event = event;
    }
}

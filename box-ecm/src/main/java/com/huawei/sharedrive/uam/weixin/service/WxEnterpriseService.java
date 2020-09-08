/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service;

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理微信中的企业数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxEnterpriseService {
    void create(WxEnterprise enterprise);
    //只更新永久授权码和状态
    void update(WxEnterprise enterprise);
    void updateState(String corpId, Byte state);
    WxEnterprise get(String corpId);
}

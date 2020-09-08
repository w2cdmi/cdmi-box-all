/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.weixin.service;


import pw.cdmi.box.disk.weixin.domain.WxUserEnterprise;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理微信企业中的用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxUserEnterpriseService {
    void create(WxUserEnterprise user);
    void deleteByUnionId(String unionId);

    WxUserEnterprise getByUnionIdAndEnterpriseId(String unionId, long enterpriseId);
    List<WxUserEnterprise> listByUnionId(String corpId);
    WxUserEnterprise getByEnterpriseUser(long userId, long enterpriseId);
}

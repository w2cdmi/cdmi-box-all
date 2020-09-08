/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.weixin.service;


import pw.cdmi.box.disk.weixin.domain.WxUser;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理微信企业中的用户数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxUserService {
    void create(WxUser user);
    void update(WxUser user);
    void delete(String openId);
    WxUser getByOpenId(String openId);
    WxUser getByUnionId(String unionId);
	WxUser getByCloudUserId(Long cloudUserId);
}

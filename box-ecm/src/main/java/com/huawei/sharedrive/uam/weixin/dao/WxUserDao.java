package com.huawei.sharedrive.uam.weixin.dao;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.List;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxUserDao</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public interface WxUserDao {
    void create(WxUser user);
    void update(WxUser user);
    void deleteByUnionId(String unionId);

    WxUser getByOpenId(String unionId);
	WxUser getByUin(String uin);
	void updateUinByUnionId(String uin, String unionId);
    WxUser getByUnionId(String unionId);
	WxUser getCloudUserId(Long cloudUserId);
	List<WxUser> listByInviterId(String inviterId, List<Order> orderList, Limit limit);
	void updateCountInvitByMe(WxUser wxUser);
	void updateCountTodayInvitByMe(WxUser wxUser);
	void cleanCountTodayInvitByMe();
	void updateCountTotalProfits(WxUser wxUser);
	void updateCountTodayProfits(WxUser wxUser);
}

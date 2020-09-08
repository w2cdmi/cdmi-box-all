package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import com.huawei.sharedrive.uam.weixin.dao.WxUserDao;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * 
 *               <pre>
 *               WxUserDao实现类
 *               </pre>
 * 
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxUserDaoImpl extends CacheableSqlMapClientDAO implements WxUserDao {
	@Override
	public void create(WxUser user) {
		sqlMapClientTemplate.insert("WxUser.insert", user);
	}

	@Override
	public void update(WxUser user) {
		sqlMapClientTemplate.update("WxUser.update", user);
	}

	@Override
	public void deleteByUnionId(String openId) {
		sqlMapClientTemplate.delete("WxUser.deleteByUnionId", openId);
	}

	@Override
	public WxUser getByOpenId(String openId) {
		return (WxUser) sqlMapClientTemplate.queryForObject("WxUser.getByOpenId", openId);
	}

	@Override
	public WxUser getByUnionId(String unionId) {
		return (WxUser) sqlMapClientTemplate.queryForObject("WxUser.getByUnionId", unionId);
	}

	@Override
	public WxUser getByUin(String uin) {
		return (WxUser) sqlMapClientTemplate.queryForObject("WxUser.getByUin", uin);
	}

	@Override
	public void updateUinByUnionId(String uin, String unionId) {
		Map<String, String> prameter = new HashMap<>();
		prameter.put("uin", uin);
		prameter.put("unionId", unionId);
		sqlMapClientTemplate.update("WxUser.updateUinByUnionId", prameter);
	}

	@Override
	public WxUser getCloudUserId(Long cloudUserId) {
		// TODO Auto-generated method stub
		return (WxUser) sqlMapClientTemplate.queryForObject("WxUser.getByCloudUserId", cloudUserId);
	}

	@Override
	public void updateCountInvitByMe(WxUser wxUser) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxUser.updateCountInvitByMe", wxUser);
	}
	
	@Override
	public void updateCountTodayInvitByMe(WxUser wxUser) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxUser.updateCountTodayInvitByMe", wxUser);
	}

	@Override
	public List<WxUser> listByInviterId(String inviterId, List<Order> orderList, Limit limit) {
		// TODO Auto-generated method stub

		Map<String, Object> map = new HashMap<String, Object>(3);
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("order", getOrderByStr(orderList));
		}
		map.put("inviterId", inviterId);
		map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("WxUser.listByInviterId", map);
	}

	private String getOrderByStr(List<Order> orderList) {
		StringBuffer orderBy = new StringBuffer();
		String field;
		for (Order order : orderList) {
			field = order.getField();
			orderBy.append(field).append(' ').append(order.getDirection()).append(',');
		}
		orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
		return orderBy.toString();
	}

	@Override
	public void cleanCountTodayInvitByMe() {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("WxUser.cleanCountTodayInvitByMe");
	}
	
	@Override
	public void updateCountTotalProfits(WxUser wxUser) {
		sqlMapClientTemplate.update("WxUser.updateCountTotalProfits", wxUser);
	}
	
	@Override
	public void updateCountTodayProfits(WxUser wxUser) {
		sqlMapClientTemplate.update("WxUser.updateCountTodayProfits", wxUser);
	}

}

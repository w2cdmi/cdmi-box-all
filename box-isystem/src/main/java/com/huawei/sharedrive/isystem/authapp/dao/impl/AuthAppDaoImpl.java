/**
 * 
 */
package com.huawei.sharedrive.isystem.authapp.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.isystem.user.domain.Admin;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
@Service
@SuppressWarnings({ "unchecked", "deprecation" })
public class AuthAppDaoImpl extends AbstractDAOImpl implements AuthAppDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getByAuthAppID(java.
	 * lang.String )
	 */
	@Override  
	public AuthApp getByAuthAppID(String authAppId) {
		AuthApp authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getByAuthAppID", authAppId);
		return authApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterd(com.
	 * huawei.sharedrive .isystem.authapp.domain.AuthApp,
	 * com.huawei.sharedrive.isystem.core.domain.Order,
	 * com.huawei.sharedrive.isystem.core.domain.Limit)
	 */
	@Override
	public List<AuthApp> getFilterd(AuthApp filter, Order order, Limit limit) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("order", order);
		map.put("limit", limit);
		List<AuthApp> appLists = sqlMapClientTemplate.queryForList("AuthApp.getFilterd", map);
		return appLists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterdCount(com.
	 * huawei .sharedrive.isystem.authapp.domain.AuthApp)
	 */
	@Override
	public int getFilterdCount(AuthApp filter) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put("filter", filter);
		return (Integer) sqlMapClientTemplate.queryForObject("AuthApp.getFilterdCount", map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#delete(java.lang.
	 * String)
	 */
	@Override
	public void delete(String authAppId) {
		sqlMapClientTemplate.delete("AuthApp.delete", authAppId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#create(com.huawei.
	 * sharedrive .isystem.authapp.domain.AuthApp)
	 */
	@Override
	public void create(AuthApp authApp) {
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		if (admin != null) {
			authApp.setCreateBy(String.valueOf(admin.getId()));
		}
		authApp.setCreatedAt(new Date());
		sqlMapClientTemplate.insert("AuthApp.insert", authApp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#updateAuthApp(com.
	 * huawei. sharedrive.isystem.authapp.domain.AuthApp)
	 */
	@Override
	public void updateAuthApp(AuthApp authApp) {
		authApp.setModifiedAt(new Date());
		sqlMapClientTemplate.update("AuthApp.updateAuthApp", authApp);
	}
	
	@Override
	public void updateAuthAppCreateby(long createby,String authAppId){
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("authAppId", authAppId);
		map.put("createBy", createby);
		sqlMapClientTemplate.update("AuthApp.updateCreateBy", map);
	}

	@Override
	public void updateStatus(String authAppId, int status) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("authAppId", authAppId);
		map.put("status", status);
		map.put("modifiedAt", new Date());
		sqlMapClientTemplate.update("AuthApp.updateStatus", map);
	}

}

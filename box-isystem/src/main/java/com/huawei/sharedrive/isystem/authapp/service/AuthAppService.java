/**
 * 
 */
package com.huawei.sharedrive.isystem.authapp.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
public interface AuthAppService {
	AuthApp getByAuthAppID(String authAppId);

	List<AuthApp> getAuthAppList(AuthApp filter, Order order, Limit limit);

	void delete(String authAppId);

	AppAccessKey create(AuthApp authApp);

	void updateAuthApp(AuthApp authApp);

	void updateStatus(String authAppId, int status);

	void updateCreate(String authAppId, long createBy);

}

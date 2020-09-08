package com.huawei.sharedrive.uam.accountuser.manager;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserAccountManager {
	void create(long userId, long enterpriseId, String authAppId,String loginName);

	/**
	 *  指定第一次登录时，是否要强制修改密码
	 */
	void create(long userId, long enterpriseId, String authAppId, boolean changePassword,String loginName);

	boolean createUserAccount(EnterpriseUser user, String authAppId, UserAccount account);

	void createByApi(long userId, long enterpriseId, String authAppId, RestUserUpdateRequest ruser);

	void update(UserAccount userAccount, String authAppId);

	UserAccount getUserAccountByApp(long userId, long enterpriseId, String authAppId);

	UserAccount getById(long id, long enterpriseId, String authAppId);

	UserAccount getByImAccount(String imAccount, long accountId);

	void fillUserAccountByApp(UserAccount userAccount, String authAppId);

	void fillUserAccountParamByApp(UserAccount userAccount, String authAppId);

	UserAccount get(long userId, long accountId);

	Page<UserAccount> getPagedUserAccount(UserAccount userAccount, String appId, long authServerId, PageRequest pageRequest, String filter);

	void deleteUserAccount(long userId, long accountId);

	boolean update(RestUserUpdateRequest ruser, UserAccount selUserAccount);

	String exportUserList(HttpServletRequest request, HttpServletResponse response, UserAccount userAccount) throws IOException;

	void updateLoginTime(UserAccount userAccount);

	UserAccount getUserByCloudUserId(long accountId, long cloudUserId);

}

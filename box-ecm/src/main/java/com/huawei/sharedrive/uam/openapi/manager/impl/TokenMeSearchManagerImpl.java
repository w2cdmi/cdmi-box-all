package com.huawei.sharedrive.uam.openapi.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.enterpriseuseraccount.manager.EnterpriseUserAccountManager;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeSearchManager;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class TokenMeSearchManagerImpl implements TokenMeSearchManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenMeSearchManager.class);

	private static final int DEFAULT_LIMIT = 100;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private LdapAuthServiceManagerImpl ldapAuthServiceManager;

	@Autowired
	private AuthServerConfigManager authServerConfigManager;

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private EnterpriseUserAccountManager enterpriseUserAccountManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Resource
	private RestClient ufmClientService;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@Override
	public ResponseSearchUser listLocalUser(Integer limit, Integer offset, String filter, long accountId,
			long enterpriseId, String appId) {
		if (offset == null) {
			offset = 0;
		}
		if (limit == null) {
			limit = DEFAULT_LIMIT;
		}
		return enterpriseUserAccountManager.listUser(limit, offset, filter, accountId, enterpriseId, appId);
	}

	@Override
	public ResponseSearchUser listADUser(String appId, String filter, Integer limit, long accountId,
			long enterpriseId) {
		List<AuthServer> list = authServerManager.getListByAccountId(enterpriseId, accountId);
		if (null == limit) {
			limit = DEFAULT_LIMIT;
		}
		if (null == list || list.size() < 1) {
			return null;
		}
		List<EnterpriseUser> enterpriseUserList = new ArrayList<EnterpriseUser>(10);
		LdapDomainConfig ldapDomainConfig;
		for (AuthServer authServer : list) {
			if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType())) {
				continue;
			}
			if (enterpriseUserList.size() >= limit) {
				break;
			}
			ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServer.getId());
			try {
				List<EnterpriseUser> selEnterpriseUserList = ldapAuthServiceManager.searchUsers(ldapDomainConfig,
						authServer.getId(), enterpriseId, filter, limit);
				duplicateRemoval(selEnterpriseUserList, enterpriseUserList, limit);
			} catch (RuntimeException e) {
				continue;
			}

		}
		List<ResponseUser> responseUserList = transToResponseUsers(enterpriseUserList, enterpriseId, appId, accountId);
		ResponseSearchUser responseSearchUser = new ResponseSearchUser();
		responseSearchUser.setLimit(limit);
		responseSearchUser.setOffset(0L);
		responseSearchUser.setTotalCount((long) enterpriseUserList.size());
		responseSearchUser.setUsers(responseUserList);
		return responseSearchUser;
	}

	@Override
	public ResponseSearchUser listADLocalUser(String appId, String filter, Integer limit, long accountId,
			long enterpriseId) {
		int adLimit = 0;
		if (null == limit) {
			limit = DEFAULT_LIMIT;
		}
		ResponseSearchUser responseLocalSearchUser = listLocalUser(limit, 0, filter, accountId, enterpriseId, appId);
		List<ResponseUser> responseLocalUserList = responseLocalSearchUser.getUsers();
		if (null != responseLocalUserList) {
			if (responseLocalUserList.size() >= limit) {
				return responseLocalSearchUser;
			}
			adLimit = limit - responseLocalUserList.size();
		}
		ResponseSearchUser searchUser = listADUser(appId, filter, adLimit, accountId, enterpriseId);
		ResponseSearchUser responseADSearchUser = null;
		List<ResponseUser> responseADUserList = null;
		if (searchUser != null) {
			responseADSearchUser = searchUser;
			responseADUserList = responseADSearchUser.getUsers();
		}
		if (null == responseLocalUserList) {
			responseLocalUserList = new ArrayList<ResponseUser>(10);
			responseLocalSearchUser.setUsers(responseLocalUserList);
		}
		duplicateRemovalResponseUser(responseADUserList, responseLocalUserList, limit);
		responseLocalSearchUser.setLimit(limit);
		return responseLocalSearchUser;
	}

	private void duplicateRemoval(List<EnterpriseUser> selEnterpriseUserList, List<EnterpriseUser> enterpriseUserList,
			int limit) {
		if (null == selEnterpriseUserList || selEnterpriseUserList.size() < 1) {
			return;
		}
		boolean isDuplicate = false;
		for (EnterpriseUser selEnterpriseUser : selEnterpriseUserList) {
			if (enterpriseUserList.size() >= limit) {
				break;
			}
			for (EnterpriseUser enterpriseUser : enterpriseUserList) {
				if (selEnterpriseUser.getObjectSid().equals(enterpriseUser.getObjectSid())) {
					isDuplicate = true;
					break;
				}
			}
			if (isDuplicate) {
				continue;
			}
			enterpriseUserList.add(selEnterpriseUser);
		}
	}

	private void duplicateRemovalResponseUser(List<ResponseUser> responseADUserList,
			List<ResponseUser> responseLocalUserList, int limit) {
		if (null == responseADUserList || responseADUserList.size() < 1) {
			return;
		}
		boolean isDuplicate = false;
		for (ResponseUser adResponseUser : responseADUserList) {
			if (responseLocalUserList.size() >= limit) {
				break;
			}
			for (ResponseUser responseUser : responseLocalUserList) {
				if (responseUser == null || adResponseUser == null || responseUser.getId() == null) {
					break;
				}
				if (responseUser.getId().equals(adResponseUser.getId())) {
					isDuplicate = true;
					break;
				}
			}
			if (isDuplicate) {
				continue;
			}
			responseLocalUserList.add(adResponseUser);
		}
	}

	private void addResponseUser(List<EnterpriseUser> enterpriseADUserList, EnterpriseUserAccount tempAccount,
			EnterpriseAccount enterpriseAccount, Enterprise enterprise, List<ResponseUser> responseUserList) {

		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		EnterpriseUserAccount enterpriseUserAccount;
		EnterpriseUser enterpriseUser;
		ResponseUser responseUser;
		UserAccount userAccount = null;
		for (EnterpriseUser adEnterpriseUser : enterpriseADUserList) {
			enterpriseUserAccount = new EnterpriseUserAccount();
			enterpriseUser = enterpriseUserManager.getByObjectSid(adEnterpriseUser.getObjectSid(),
					tempAccount.getEnterpriseId());
			if (null != enterpriseUser) {
				adEnterpriseUser.setId(enterpriseUser.getId());
				userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(),
						tempAccount.getEnterpriseId(), tempAccount.getAppId());
				if (null != userAccount) {
					EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
					RestUserCreateRequest restUserCreateRequest = userHttpClient
							.getUserInfo(userAccount.getCloudUserId(), enterpriseAccount);
					if (restUserCreateRequest != null) {
						enterpriseUserAccount.setSpaceUsed(restUserCreateRequest.getSpaceUsed());
						enterpriseUserAccount.setFileCount(restUserCreateRequest.getFileCount());
					}
				}
			}
			EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, adEnterpriseUser);
			responseUser = ResponseUser.convetToResponseUser(enterpriseUserAccount, tempAccount.getAppId());
			responseUser.setAppId(tempAccount.getAppId());
			responseUser.setDomain(enterprise.getDomainName());
			responseUser.setEnterpriseId(tempAccount.getEnterpriseId());
			responseUserList.add(responseUser);
		}
	}

	private List<ResponseUser> transToResponseUsers(List<EnterpriseUser> enterpriseADUserList, long enterpriseId,
			String appId, long accountId) {
		List<ResponseUser> responseUserList = new ArrayList<ResponseUser>(10);
		if (null == enterpriseADUserList || enterpriseADUserList.size() < 1) {
			return null;
		}
		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		if (null == enterprise) {
			LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
			throw new BusinessException();
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
		if (null == enterpriseAccount) {
			LOGGER.error("enterpriseAccount is null accountId:" + accountId);
			throw new BusinessException();
		}
		EnterpriseUserAccount tempAccount = new EnterpriseUserAccount();
		tempAccount.setEnterpriseId(enterpriseId);
		tempAccount.setAppId(appId);
		addResponseUser(enterpriseADUserList, tempAccount, enterpriseAccount, enterprise, responseUserList);
		return responseUserList;
	}
}

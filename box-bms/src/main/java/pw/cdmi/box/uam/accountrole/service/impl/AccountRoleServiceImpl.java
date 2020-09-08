package pw.cdmi.box.uam.accountrole.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.accountrole.dao.AccountRoleDao;
import pw.cdmi.box.uam.accountrole.domain.AccountRole;
import pw.cdmi.box.uam.accountrole.service.AccountRoleService;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.httpclient.domain.RestNodeRoleInfo;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.JsonUtils;

@Service
public class AccountRoleServiceImpl implements AccountRoleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountRoleServiceImpl.class);

	@Autowired
	private AccountRoleDao accountRoleDao;

	@Autowired
	private EnterpriseAccountDao enterpriseAccountDao;

	@Resource
	private RestClient ufmClientService;

	@Override
	public void create(long accountId, String roleId) {
		AccountRole accountRole = new AccountRole();
		accountRole.setResourceRole(roleId);
		accountRole.setAccountId(accountId);
		accountRoleDao.create(accountRole);
	}

	@Override
	public int delete(long accountId, String roleId) {
		return accountRoleDao.delete(accountId, roleId);
	}

	@Override
	public List<AccountRole> getList(long accountId) {
		return accountRoleDao.getList(accountId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RestNodeRoleInfo> getSystemRoles(String appId, String secretKey, String secretKeyEncode,
			String accessKey) {
		List<RestNodeRoleInfo> result;

		try {
			String uri = "/api/v2/roles";
			Map<String, String> headerMap = assembleAccountToken(appId, secretKey, secretKeyEncode, accessKey);
			TextResponse response = ufmClientService.performGetText(uri, headerMap);
			String content = response.getResponseBody();

			if (response.getStatusCode() == HttpStatus.OK.value()) {
				result = (List<RestNodeRoleInfo>) JsonUtils.stringToList(content, ArrayList.class,
						RestNodeRoleInfo.class);

				LOGGER.info(ToStringBuilder.reflectionToString(result));
				return result;
			}
			LOGGER.error(response.getResponseBody());
			return null;
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private Map<String, String> assembleAccountToken(String appId, String secretKey, String secretKeyEncode,
			String accessKey) {
		String decodedKey = EDToolsEnhance.decode(secretKey, secretKeyEncode);
		Map<String, String> headers = new HashMap<String, String>(16);
		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		String sign = SignatureUtils.getSignature(decodedKey, dateStr);
		String authorization = "account," + accessKey + ',' + sign;
		headers.put("Authorization", authorization);
		headers.put("Date", dateStr);
		return headers;
	}

	@Override
	public void configAllRoles(String appId, long enterpriseId) {
		EnterpriseAccount enterprise = enterpriseAccountDao.getByEnterpriseApp(enterpriseId, appId);
		if (null == enterprise) {
			throw new InvalidParamterException("enterprise is null");
		}
		List<RestNodeRoleInfo> nodeRoleInfos = getSystemRoles(appId, enterprise.getSecretKey(),
				enterprise.getSecretKeyEncodeKey(), enterprise.getAccessKeyId());
		for (RestNodeRoleInfo roleInfo : nodeRoleInfos) {
			if ("auther".equalsIgnoreCase(roleInfo.getName()) || "lister".equalsIgnoreCase(roleInfo.getName())
					|| "prohibitVisitors".equalsIgnoreCase(roleInfo.getName())) {
				continue;
			}
			create(enterprise.getAccountId(), roleInfo.getName());
		}

	}

}

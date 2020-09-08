package pw.cdmi.box.disk.accountbaseconfig.server.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.accountbaseconfig.dao.AccountBasicConfigDao;
import pw.cdmi.box.disk.accountbaseconfig.domain.AccountBasicConfig;
import pw.cdmi.box.disk.accountbaseconfig.server.AccountBasicConfigService;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.SystemConfig;

@Service
public class AccountBasicConfigServiceImpl implements AccountBasicConfigService {
	@Autowired
	private AccountBasicConfigDao accountBasicConfigDao;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	@Override
	public AccountBasicConfig get(AccountBasicConfig appBasicConfig, String appId) {
		AccountBasicConfig accountBasicConfig = accountBasicConfigDao.get(appBasicConfig);
		if (accountBasicConfig == null) {
			accountBasicConfig = new AccountBasicConfig();
			accountBasicConfig.setAccountId(appBasicConfig.getAccountId());
			List<SystemConfig> itemList = systemConfigDAO.getByPrefix(appId, null, AppBasicConfig.BASIC_CONFIG_PREFIX);
			AppBasicConfig basicConfig = AppBasicConfig.buildAppBasicConfig(itemList);
			accountBasicConfig.setAccountId(appBasicConfig.getAccountId());
			accountBasicConfig.setUserSpaceQuota(basicConfig.getUserSpaceQuota() + "");
			accountBasicConfig.setUserVersions(basicConfig.getMaxFileVersions() + "");
			accountBasicConfig.setEnableTeamSpace(basicConfig.isEnableTeamSpace());
			accountBasicConfig.setMaxTeamSpaces(basicConfig.getMaxTeamSpaces() + "");
			accountBasicConfig.setTeamSpaceQuota(basicConfig.getTeamSpaceQuota() + "");
			accountBasicConfig.setTeamSpaceVersions(basicConfig.getMaxFileVersions() + "");
		}
		return accountBasicConfig;
	}

}

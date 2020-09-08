package pw.cdmi.box.disk.accountbaseconfig.server;

import pw.cdmi.box.disk.accountbaseconfig.domain.AccountBasicConfig;

public interface AccountBasicConfigService {

	AccountBasicConfig get(AccountBasicConfig appBasicConfig,String appId);
}

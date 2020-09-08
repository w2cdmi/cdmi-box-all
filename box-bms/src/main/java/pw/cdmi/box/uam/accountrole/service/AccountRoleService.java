package pw.cdmi.box.uam.accountrole.service;

import java.util.List;

import pw.cdmi.box.uam.accountrole.domain.AccountRole;
import pw.cdmi.box.uam.httpclient.domain.RestNodeRoleInfo;

public interface AccountRoleService {
	void create(long accountId, String roleId);

	int delete(long accountId, String roleId);

	List<AccountRole> getList(long accountId);

	public List<RestNodeRoleInfo> getSystemRoles(String appId, String secretKey, String secretKeyEncode,
			String accessKey);

	public void configAllRoles(String appId, long enterpriseId);
}

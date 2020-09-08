package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseDao {

	long create(Enterprise enterprise);

	long queryMaxExecuteRecordId();

	boolean isDuplicateValues(Enterprise enterprise);

	int getFilterdCount(String filter, String appId);

	List<Enterprise> getFilterd(String filter, String appId, Order order, Limit limit);

	Enterprise getById(long id);

	long getByDomainExclusiveId(Enterprise enterprise);

	void updateEnterpriseInfo(Enterprise enterprise);

	void updateStatus(Enterprise enterprise);

	void updateNetworkAuthStatus(Byte networkAuthStatus, Long id);

	void deleteById(long id);

	Enterprise getByDomainName(String domainName);

	List<Enterprise> listForUpdate();

	Enterprise getByName(String name);
}

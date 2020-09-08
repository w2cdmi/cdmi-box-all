package pw.cdmi.box.uam.enterprise.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseService {

	long create(Enterprise enterprise);

	boolean isDuplicateValues(Enterprise enterprise);

	int getFilterdCount(String filter, String appId, Integer status);

	Page<Enterprise> getFilterd(String filter, Integer status, String appId, PageRequest pageRequest);

	List<Enterprise> getByCombOrder(String filter, Integer status, String order, Limit limit);

	/** 根据企业的ID获取该企业信息 **/
	Enterprise getById(long id);
	
	/** 获得业务系统的部署企业信息 **/
	Enterprise getByOwnerId(long id);

	long getByDomainExclusiveId(Enterprise enterprise);

	void updateEnterpriseInfo(Enterprise enterprise);

	void updateStatus(Enterprise enterprise);

	void deleteById(long id);

	Enterprise getByDomainName(String domainName);

	void updateNetworkAuthStatus(Byte networkAuthStatus, Long id);

	List<Enterprise> listForUpdate();

	Enterprise getByContactEmail(String email);
	
	Enterprise getByContactPhone(String phone);

}

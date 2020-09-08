package pw.cdmi.box.uam.enterprise.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseDao;
import pw.cdmi.common.domain.enterprise.Enterprise;

@SuppressWarnings({ "unchecked", "deprecation" })
@Component
public class EnterpriseDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseDao {

	@Override
	public long create(Enterprise enterprise) {
		Date now = new Date();
		enterprise.setCreatedAt(now);
		enterprise.setModifiedAt(now);
		if(enterprise.getOwnerId() < 1){
			Enterprise owner = getByOwnerId(-1); // -1代表是运营企业
			if(owner != null){
			    enterprise.setOwnerId(owner.getId());
			}
		}
		sqlMapClientTemplate.insert("Enterprise.insert", enterprise);
		return enterprise.getId();
	}

	@Override
	public boolean isDuplicateValues(Enterprise enterprise) {
		int count = (int) sqlMapClientTemplate.queryForObject("Enterprise.getDuplicateValues", enterprise);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public int getFilterdCount(String filter, String appId, Integer status) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		if (StringUtils.isNotBlank(filter)) {
			map.put("filter", filter);
		}
		if (StringUtils.isNotBlank(appId)) {
			map.put("appId", appId);
		}

		return (Integer) sqlMapClientTemplate.queryForObject("Enterprise.getFilterdCount", map);
	}

	@Override
	public List<Enterprise> getFilterd(String filter, Integer status, String appId, Order order, Limit limit) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		if (StringUtils.isNotBlank(filter)) {
			map.put("filter", filter);
		}
		if (StringUtils.isNotBlank(appId)) {
			map.put("appId", appId);
		}
		if (status != null) {
			map.put("status", status);
		}
		map.put("order", order);
		map.put("limit", limit);

		return sqlMapClientTemplate.queryForList("Enterprise.getFilterd", map);
	}

	@Override
	public List<Enterprise> getByCombOrder(String filter, Integer status, String order, Limit limit) {
		Map<String, Object> map = new HashMap<String, Object>(4);
		if (StringUtils.isNotBlank(filter)) {
			map.put("filter", filter);
		}
		if (status != null) {
			map.put("status", status);
		}
		map.put("order", order);
		map.put("limit", limit);

		return sqlMapClientTemplate.queryForList("Enterprise.getByCombOrder", map);
	}

	@Override
	public Enterprise getById(long id) {
		if (isCacheSupported()) {
			String key = Enterprise.getEnterpriseIdCacheKey(id);
			Enterprise enterprise = (Enterprise) getCacheClient().getCache(key);
			if (enterprise != null) {
				return enterprise;
			}
			enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getById", id);
			if (enterprise == null) {
				return null;
			}
			getCacheClient().setCache(key, enterprise);
			return enterprise;
		}

		return (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getById", id);
	}

	@Override
	public Enterprise getByOwnerId(long id) {
		return (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByOwnerId", id);
	}
	
	@Override
	public void updateEnterpriseInfo(Enterprise enterprise) {
		Enterprise selenterprise = getById(enterprise.getId());
		if (null == selenterprise) {
			return;
		}
		enterprise.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Enterprise.updateEnterpriseInfo", enterprise);
		deleteCacheAfterCommit(Enterprise.getEnterpriseDomainCacheKey(selenterprise.getDomainName()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseIdCacheKey(selenterprise.getId()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseListKey());

	}

	@Override
	public void updateStatus(Enterprise enterprise) {
		Enterprise selenterprise = getById(enterprise.getId());
		if (null == selenterprise) {
			return;
		}
		enterprise.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Enterprise.updateStatus", enterprise);
		deleteCacheAfterCommit(Enterprise.getEnterpriseDomainCacheKey(selenterprise.getDomainName()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseIdCacheKey(selenterprise.getId()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseListKey());

	}

	@Override
	public void updateNetworkAuthStatus(Byte networkAuthStatus, Long id) {
		Enterprise selenterprise = getById(id);
		if (null == selenterprise) {
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("networkAuthStatus", networkAuthStatus);
		map.put("id", id);
		sqlMapClientTemplate.update("Enterprise.updateNetworkAuthStatus", map);
		deleteCacheAfterCommit(Enterprise.getEnterpriseDomainCacheKey(selenterprise.getDomainName()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseIdCacheKey(selenterprise.getId()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseListKey());

	}

	@Override
	public long getByDomainExclusiveId(Enterprise enterprise) {
		long count = (long) sqlMapClientTemplate.queryForObject("Enterprise.getByDomainExclusiveId", enterprise);
		return count;
	}

	@Override
	public void deleteById(long id) {
		Enterprise enterprise = getById(id);
		if (null == enterprise) {
			return;
		}
		sqlMapClientTemplate.delete("Enterprise.deleteById", id);
		deleteCacheAfterCommit(Enterprise.getEnterpriseDomainCacheKey(enterprise.getDomainName()));
		deleteCacheAfterCommit(Enterprise.getEnterpriseIdCacheKey(id));
		deleteCacheAfterCommit(Enterprise.getEnterpriseListKey());
	}

	@Override
	public Enterprise getByDomainName(String domainName) {
		if (isCacheSupported()) {
			String key = Enterprise.getEnterpriseDomainCacheKey(domainName);
			Enterprise enterprise = (Enterprise) getCacheClient().getCache(key);
			if (enterprise != null) {
				return enterprise;
			}
			enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByDomainName", domainName);
			if (enterprise == null) {
				return null;
			}
			getCacheClient().setCache(key, enterprise);
			return enterprise;
		}
		Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByDomainName",
				domainName);
		return enterprise;
	}

	@Override
	public long queryMaxExecuteRecordId() {
		Object maxId = sqlMapClientTemplate.queryForObject("Enterprise.getMaxId");
		if (maxId == null) {
			return 0L;
		}
		return (Long) maxId;
	}

	@Override
	public List<Enterprise> listForUpdate() {
		return sqlMapClientTemplate.queryForList("Enterprise.listForUpdate");
	}

	@Override
	public Enterprise getByContactEmail(String email) {
		Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByContactEmail", email);
		return enterprise;
	}

	@Override
	public Enterprise getByPhone(String phone) {
		Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByContactPhone", phone);
		return enterprise;
	}
}

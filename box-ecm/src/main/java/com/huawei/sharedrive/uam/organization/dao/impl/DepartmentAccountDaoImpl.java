package com.huawei.sharedrive.uam.organization.dao.impl;


import com.huawei.sharedrive.uam.organization.dao.DepartmentAccountDao;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class DepartmentAccountDaoImpl extends CacheableSqlMapClientDAO implements DepartmentAccountDao {
    @Autowired
    DepartmentAccountIdGenerator idGenerator;

    @Override
    public long getMaxId() {
        Object maxId = sqlMapClientTemplate.queryForObject("DepartmentAccount.getMaxId");
        if (maxId == null) {
            return 0L;
        }
        return (Long) maxId;
    }


    @Override
    public void create(DepartmentAccount deptAccount) {
        long id = idGenerator.getNextId();
        deptAccount.setId(id);
        deptAccount.setCreatedAt(new Date());
        sqlMapClientTemplate.insert("DepartmentAccount.insert", deptAccount);
    }

    @Override
    public void update(DepartmentAccount deptAccount) {
        sqlMapClientTemplate.update("DepartmentAccount.update", deptAccount);
    }

    @Override
    public void deleteById(long id) {
        sqlMapClientTemplate.delete("DepartmentAccount.deleteById", id);
    }

    @Override
    public void deleteByDeptIdAndAccountId(DepartmentAccount deptAccount) {
        sqlMapClientTemplate.delete("DepartmentAccount.deleteByDeptIdAndAccountId", deptAccount);
    }

    @Override
    public DepartmentAccount getById(long id, long accountId) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);

        return (DepartmentAccount) sqlMapClientTemplate.queryForObject("DepartmentAccount.getById", map);
    }

    @Override
    public DepartmentAccount getByDeptIdAndAccountId(long deptId, long accountId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("deptId", deptId);
        map.put("accountId", accountId);
        return (DepartmentAccount) sqlMapClientTemplate.queryForObject("DepartmentAccount.get", map);
    }




















    @Override
    public DepartmentAccount getByCloudDepartmentAccountId(DepartmentAccount deptAccount) {
        long accountId = deptAccount.getAccountId();
        return (DepartmentAccount) sqlMapClientTemplate.queryForObject("DepartmentAccount.getBycloudDepartmentAccountId",
                deptAccount);
    }

    @Override
    public int countFiltered(long accountId, long enterpriseId, long userSource, String filter, Integer status) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("accountId", accountId);
        map.put("enterpriseId", enterpriseId);
        map.put("userSource", userSource);
        map.put("status", status);
        if (StringUtils.isNotBlank(filter)) {
            map.put("filter", filter);
        }

        Object count = sqlMapClientTemplate.queryForObject("DepartmentAccount.countFiltered", map);
        if (null == count) {
            return 0;
        }

        return (int) count;
    }

    @Override
    public List<DepartmentAccount> getFilterd(DepartmentAccount deptAccount, long userSource, Limit limit, String filter) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("accountId", deptAccount.getAccountId());
        map.put("enterpriseId", deptAccount.getEnterpriseId());
        map.put("status", deptAccount.getStatus());
        map.put("userSource", userSource);
        map.put("limit", limit);
        if (StringUtils.isNotBlank(filter)) {
            map.put("filter", filter);
        }
        return sqlMapClientTemplate.queryForList("DepartmentAccount.getFilterd", map);
    }

    @Override
    public void updateStatus(DepartmentAccount deptAccount, String ids) {
        deptAccount.setModifiedAt(new Date());

        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("ids", ids);
        map.put("filter", deptAccount);
        sqlMapClientTemplate.update("DepartmentAccount.updateStatus", map);
    }

    @Override
    public void updateRole(DepartmentAccount deptAccount, String ids) {
        deptAccount.setModifiedAt(new Date());

        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("ids", ids);
        map.put("filter", deptAccount);
        sqlMapClientTemplate.update("DepartmentAccount.updateRole", map);
    }

    @Override
    public void updateUserIdById(DepartmentAccount deptAccount) {
        deptAccount.setModifiedAt(new Date());

        sqlMapClientTemplate.update("DepartmentAccount.updateUserIdById", deptAccount);
    }

    @Override
    public List<DepartmentAccount> listByAccountIdAndDeptId(long accountId, List<Long> deptList) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("accountId", accountId);
        map.put("deptList", deptList);

        return sqlMapClientTemplate.queryForList("DepartmentAccount.listByAccountIdAndDeptId", map);
    }


	@Override
	public DepartmentAccount getByDeptIdAndEnterpriseId(long deptId, Long enterpriseId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("deptId", deptId);
        map.put("enterpriseId", enterpriseId);

        return (DepartmentAccount) sqlMapClientTemplate.queryForObject("DepartmentAccount.getByDeptIdAndEnterpriseId", map);
	}


	@Override
	public void deleteBydeptId(long deptid, long enterpriseId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("deptId", deptid);
        map.put("enterpriseId", enterpriseId);
        sqlMapClientTemplate.delete("DepartmentAccount.deleteBydeptId",map);
	}
}

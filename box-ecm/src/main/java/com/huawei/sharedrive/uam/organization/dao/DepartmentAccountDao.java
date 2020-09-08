package com.huawei.sharedrive.uam.organization.dao;

import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import pw.cdmi.box.domain.Limit;

import java.util.List;

public interface DepartmentAccountDao {
    long getMaxId();

    void create(DepartmentAccount deptAccount);

    void update(DepartmentAccount deptAccount);

    void deleteById(long id);

    void deleteByDeptIdAndAccountId(DepartmentAccount deptAccount);

    DepartmentAccount getById(long id, long accountId);

    DepartmentAccount getByDeptIdAndAccountId(long userId, long accountId);

    DepartmentAccount getByCloudDepartmentAccountId(DepartmentAccount deptAccount);

    int countFiltered(long accountId, long enterpriseId, long userSource, String filter, Integer status);

    List<DepartmentAccount> getFilterd(DepartmentAccount deptAccount, long userSource, Limit limit, String filter);

    void updateStatus(DepartmentAccount deptAccount, String ids);

    void updateRole(DepartmentAccount deptAccount, String ids);

    void updateUserIdById(DepartmentAccount deptAccount);

    List<DepartmentAccount> listByAccountIdAndDeptId(long accountId, List<Long>deptList);

	DepartmentAccount getByDeptIdAndEnterpriseId(long deptId, Long enterpriseId);

	void deleteBydeptId(long deptid, long enterpriseId);
}

package com.huawei.sharedrive.uam.organization.service;

import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AppBasicConfig;

import java.util.List;

public interface DepartmentAccountService {
    void create(DepartmentAccount deptAccount);

    void update(DepartmentAccount deptAccount);

    DepartmentAccount getByDeptIdAndAccountId(long deptId, long accountId);

    void delete(long id);

    DepartmentAccount getById(long id, long accountId);

    void buildDepartmentAccount(DepartmentAccount deptAccount, AppBasicConfig appBasicConfig);

    void buildDepartmentAccountParam(DepartmentAccount deptAccount, AppBasicConfig appBasicConfig);

    int countFiltered(long accountId, long enterpriseId, long userSource, String filter, Integer status);

    List<DepartmentAccount> getFiltered(DepartmentAccount deptAccount, long userSource, Limit limit, String filter);

    void updateStatus(DepartmentAccount deptAccount, String ids);

    DepartmentAccount getByCloudDepartmentAccountId(DepartmentAccount deptAccount);

    void updateUserIdById(DepartmentAccount deptAccount);

    //返回指定Account下，多个部门对应的DepartmentAccount
    List<DepartmentAccount> listByAccountIdAndDeptId(long accountId, List<Long> deptList);

	DepartmentAccount getByDeptIdAndEnterpriseId(long deptId, Long enterpriseId);

	void deleteBydeptId(long deptid, long enterpriseId);
}

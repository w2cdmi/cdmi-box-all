package com.huawei.sharedrive.app.user.service.impl;

import com.huawei.sharedrive.app.user.dao.DepartmentDao;
import com.huawei.sharedrive.app.user.domain.*;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	DepartmentDao departmentDao;

    @Override
    public Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long departmentId) {
        return departmentDao.getByEnterpriseIdAndDepartmentId(enterpriseId, departmentId);
    }

    @Override
    public Department getByEnterpriseIdAndDepartmentCloudUserId(long enterpriseId, long departmentCloudUserId) {
        return departmentDao.getByEnterpriseIdAndDepartmentCloudUserId(enterpriseId, departmentCloudUserId);
    }

    @Override
	public List<Long> getDeptCloudUserIdByCloudUserId(long enterpriseId, long cloudUserId, long accountId) {
		return departmentDao.getDeptCloudUserIdByCloudUserId(enterpriseId, cloudUserId, accountId);
	}

	@Override
	public List<UserAccount> getUsersByDept(long deptId, String enterpriseId, Long accountId) {
		// TODO Auto-generated method stub
		return departmentDao.getUsersByDept(deptId,enterpriseId,accountId);
	}

    @Override
    public List<DepartmentAccount> listUserDepts(long enterpriseUserId, Long enterpriseId) {
        return departmentDao.listUserDepts(enterpriseUserId,enterpriseId);
    }

    @Override
    public List<EnterpriseSecurityPrivilege> listPrivilege(EnterpriseSecurityPrivilege filter) {
        return departmentDao.listPrivilege(filter);
    }

    @Override
    public long getUserCloudIdByEnterpriseUserId(long enterpriseUserId, long accountId) {
        return departmentDao.getUserCloudIdByEnterpriseUserId(enterpriseUserId,accountId);
    }

	@Override
	public void updateEnterpriseUserStatus(long enterpriseUserId, Long enterpriseId, byte status) {
		// TODO Auto-generated method stub
		departmentDao.updateEnterpriseUserStatus(enterpriseUserId, enterpriseId, status);
	}

	@Override
	public UserAccount getUserAccountByCloudUserId(long cloudUserId, long accountId) {
		// TODO Auto-generated method stub
		return departmentDao.getUserAccountByCloudUserId(cloudUserId, accountId);
	}
}

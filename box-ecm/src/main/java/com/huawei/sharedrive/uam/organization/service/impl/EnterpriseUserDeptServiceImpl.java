package com.huawei.sharedrive.uam.organization.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.organization.dao.EnterpriseUserDeptDao;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;

@Service
public class EnterpriseUserDeptServiceImpl implements EnterpriseUserDeptService{
	
	@Autowired
	private EnterpriseUserDeptDao userDeptDao;

	@Override
	public List<EnterpriseUserDept> getByEnterPriseUser(EnterpriseUserDept enterpriseUserDept) {
		return userDeptDao.getByEnterpriseIdAndUserId(enterpriseUserDept);
	}

	@Override
	public List<EnterpriseUserDept> getByEnterpriseIdAndDeptId(long enterpriseId, long deptId) {
		return userDeptDao.getByEnterpriseIdAndDeptId(enterpriseId, deptId);
	}

	@Override
	public void create(EnterpriseUserDept userDept) {
		if(userDept.getCreatedAt() == null) {
			userDept.setCreatedAt(new Date());
		}
		userDeptDao.create(userDept);
	}

	@Override
	public void deleteByEnterpriseId(long enterpriseId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);

		userDeptDao.delete(userDept);
	}

	@Override
	public void deleteByDepartmentId(long enterpriseId, long departmentId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);
		userDept.setDepartmentId(departmentId);

		userDeptDao.delete(userDept);
	}

	@Override
	public void deleteByEnterpriseUserId(long enterpriseId, long enterpriseUserId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);
		userDept.setEnterpriseUserId(enterpriseUserId);

		userDeptDao.delete(userDept);
	}

	@Override
	public void deleteByDepartmentIdAndEnterpriseUserId(long enterpriseId, long departmentId, long enterpriseUserId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);
		userDept.setDepartmentId(departmentId);
		userDept.setEnterpriseUserId(enterpriseUserId);

		userDeptDao.delete(userDept);
	}

	public void deleteByEnterpriseIdAndUserId(long enterpriseId, long userId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);
		userDept.setEnterpriseUserId(userId);
		userDeptDao.delete(userDept);
	}

	public void fixDepartmentId(long enterpriseId, long userId) {
		EnterpriseUserDept userDept = new EnterpriseUserDept();
		userDept.setEnterpriseId(enterpriseId);
		userDept.setEnterpriseUserId(userId);

		//先删除所有不可见的部门
		userDeptDao.deleteNonexistentDept(userDept);

		//如果所有的部门都不可见，生成根部门
		userDeptDao.createRootDeptForNonexistentDept(userDept);
	}

	@Override
	public void update(EnterpriseUserDept userDept) {
		userDeptDao.update(userDept);
	}
}

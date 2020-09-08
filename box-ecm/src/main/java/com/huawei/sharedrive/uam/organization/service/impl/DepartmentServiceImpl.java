package com.huawei.sharedrive.uam.organization.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.organization.dao.DepartmentDao;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	DepartmentDao departmentDao;

	@Override
	public Long create(Department department) {
		if(department.getDepartmentId() == null) {
			//没有设置部门ID，使用自增
			department.setDepartmentId(getNextId(department.getEnterpriseId()));
		}

		if(department.getParentId() == null) {
			department.setParentId(0L);
		}

        if(department.getDomain() == null) {
            department.setDomain("local");
        }

		if(department.getCreatedAt() == null) {
			department.setCreatedAt(new Date());
		}

		if(department.getState() == null) {
			department.setState(Department.STATE_ENABLE);
		}

		return departmentDao.createDept(department);
	}

    @Override
    public void update(Department dept) {
        if(dept.getModifiedAt() == null) {
            dept.setModifiedAt(new Date());
        }

        departmentDao.updateDept(dept);
    }

    @Override
    public void changeState(long enterpriseId, byte state) {
        Department department = new Department();

        department.setEnterpriseId(enterpriseId);
        department.setState(state);
        department.setModifiedAt(new Date());

        departmentDao.updateStateByEnterpriseId(department);
    }

    @Override
    public void delete(long enterpriseId, long departmentId) {
        Department dept = new Department();
        dept.setEnterpriseId(enterpriseId);
        dept.setDepartmentId(departmentId);

		departmentDao.deleteDept(dept);
    }

    @Override
	public List<Department> listDeptTreeByEnterpriseId(Long enterpriseId) {
		List<Department> list = null;
		if (null != enterpriseId) {
			list = departmentDao.getByEnterpriseId(enterpriseId);
		}
		return list;
	}

	@Override
	public Department getEnpDeptByNameAndParent(Long enterpriseid, String name, Long pId) {
		Department dept = null;
		if (enterpriseid != null) {
			dept = departmentDao.getEnpDeptByNameAndParent(enterpriseid, name, pId);
		}
		return dept;
	}

	@Override
	public Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long departmentId) {
		return departmentDao.getById(enterpriseId, departmentId);
	}

	public List<Department> listRootDepartmentByEnterpriseId(Long enterpriseId) {
		List<Department> list = null;
		if (null != enterpriseId) {
			list = departmentDao.listRootDeptByEnterpriseId(enterpriseId);
		}
		return list;
	}

	public List<Department> listAllDepartmentByEnterpriseId(long enterpriseId) {
		return departmentDao.listAllDeptByEnterpriseId(enterpriseId);
	}

	@Override
	public List<Department> listDepByParentDepId(long parentDepId, long enterpriseId) {
		return departmentDao.listDepByParentDepId(parentDepId,enterpriseId);
	}

	@Override
	public List<Department> getByEnterpriseIdAndState(long enterpriseId, byte state) {
		return departmentDao.getByEnterpriseIdAndState(enterpriseId, state);
	}

	@Override
	public List<Long> listDeptIdByParentDepId(long parentDepId, long enterpriseId) {
		return departmentDao.listDeptIdByParentDepId(parentDepId, enterpriseId);
	}

	@Override
	public List<Long> listDeptHierarchyOfDept(long enterpriseId, long deptId) {
		List<Long> hierarchy = new ArrayList<>();

		Department dept = departmentDao.getById(enterpriseId, deptId);
		if(dept == null) {
			return hierarchy;
		}

		hierarchy.add(dept.getDepartmentId());

		while ((dept = getByEnterpriseIdAndDepartmentId(enterpriseId, dept.getParentId())) != null) {
			hierarchy.add(dept.getDepartmentId());
		}

		return hierarchy;
	}

	/**
	 * 部门ID在企业内唯一
	 */
	private long getNextId(long enterpriseId) {
		long maxId = departmentDao.getMaxDepartmentIdInEnterprise(enterpriseId);
		return maxId + 1;
	}

	//如果企业微信通讯录没有设置全部范围可见，同步后的部分部门，其他父部门不可见，需要设置为0，让其他作为root部门显示。
	public void fixParentId(long enterpriseId) {
		List<Department> deptList = departmentDao.getByEnterpriseId(enterpriseId);

		//缓存所有的部门ID
		Set<Long> set = new HashSet<>();
		for(Department dept : deptList) {
			set.add(dept.getDepartmentId());
		}

		for(Department dept: deptList) {
			//parentId不在缓存中，将其设置为0
			if(dept.getParentId() > 0 && !set.contains(dept.getParentId())) {
				dept.setParentId(0L);
				update(dept);
			}
		}
	}

	@Override
	public List<Long> getDeptCloudUserIdByUserId(long enterpriseId, long enterpriseUserId, long accountId) {
		return departmentDao.getDeptCloudUserIdByUserId(enterpriseId, enterpriseUserId, accountId);
	}


	//获取某个用户所在的部门数量
	@Override
	public int countByEnterpriseUserId(long enterpriseId, long enterpriseUserId) {
		return departmentDao.countByEnterpriseUserId(enterpriseId, enterpriseUserId);
	}

	//获取某个用户所在的部门列表
	@Override
	public List<Department> getByEnterpriseUserId(long enterpriseId, long enterpriseUserId) {
		return departmentDao.getByEnterpriseUserId(enterpriseId, enterpriseUserId);
	}

	@Override
	public List<Department> search(long enterpriseId, String name) {
		// TODO Auto-generated method stub
		return departmentDao.search(enterpriseId,name);
	}
}

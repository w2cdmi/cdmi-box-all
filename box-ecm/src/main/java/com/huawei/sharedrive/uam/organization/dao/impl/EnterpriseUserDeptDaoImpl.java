package com.huawei.sharedrive.uam.organization.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.organization.dao.EnterpriseUserDeptDao;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Repository
public class EnterpriseUserDeptDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseUserDeptDao{

	@Override
	public List<EnterpriseUserDept> getByEnterpriseIdAndUserId(EnterpriseUserDept enterpriseUserDept) {
		return sqlMapClientTemplate.queryForList("EnterpriseUserDept.getByEnterpriseIdAndUserId",enterpriseUserDept);
	}

	@Override
	public List<EnterpriseUserDept> getByEnterpriseIdAndDeptId(long enterpriseId, long departmentId) {
		return sqlMapClientTemplate.queryForList("EnterpriseUserDept.getByDeptId",departmentId);
	}
	

	public List<EnterpriseUserDept> get(EnterpriseUserDept enterpriseUserDept) {
		return sqlMapClientTemplate.queryForList("EnterpriseUserDept.get",enterpriseUserDept);
	}
	
	@Override
	public void create(EnterpriseUserDept enterpriseUserDept){
		if(get(enterpriseUserDept).size()==0){
			sqlMapClientTemplate.insert("EnterpriseUserDept.create",enterpriseUserDept);
		}
		
	}

	@Override
	public void delete(EnterpriseUserDept enterpriseUserDept){
		sqlMapClientTemplate.delete("EnterpriseUserDept.delete",enterpriseUserDept);
	}

	//删除企业下所有不存在的部门关系
	@Override
	public void deleteNonexistentDept(EnterpriseUserDept userDept) {
		sqlMapClientTemplate.delete("EnterpriseUserDept.deleteNonexistentDept", userDept);
	}

	public void createRootDeptForNonexistentDept(EnterpriseUserDept userDept) {
		sqlMapClientTemplate.insert("EnterpriseUserDept.createRootDeptForNonexistentDept", userDept);
	}

	@Override
	public void update(EnterpriseUserDept userDept) {
		sqlMapClientTemplate.update("EnterpriseUserDept.update",userDept);
	}
}

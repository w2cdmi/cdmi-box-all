package com.huawei.sharedrive.uam.organization.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;

@Repository
public interface EnterpriseUserDeptDao {
	public List<EnterpriseUserDept> getByEnterpriseIdAndDeptId(long enterpriseId, long  deptId);

	List<EnterpriseUserDept> getByEnterpriseIdAndUserId(EnterpriseUserDept enterpriseUserDept);

	void create(EnterpriseUserDept EnterpriseUserDept);

	public void delete(EnterpriseUserDept userDept);

	//删除企业下(或某个用户下)所有不存在的部门关系
	public void deleteNonexistentDept(EnterpriseUserDept userDept);

	//为父部门都不可见的用户，生成根部门记录
	public void createRootDeptForNonexistentDept(EnterpriseUserDept userDept);

    void update(EnterpriseUserDept userDept);
}

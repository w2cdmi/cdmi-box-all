package com.huawei.sharedrive.uam.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.domain.DeptNode;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;

import java.util.List;

public class TreeNodeUtil {

	
	public static DeptNode convertUser2Node(long deptId, EnterpriseUser enterpriseUser, UserAccount userAccount) {
		ResponseUser responseUser = ResponseUser.convetToResponseUser(enterpriseUser, userAccount);
		DeptNode mj = new DeptNode();
		mj.setId(String.valueOf(userAccount.getCloudUserId()));
		mj.setUserId(String.valueOf(responseUser.getId()));
		mj.setpId(String.valueOf(deptId));
		mj.setName(enterpriseUser.getName());
		mj.setEmail(enterpriseUser.getEmail());
		mj.setAlias(enterpriseUser.getAlias());
		mj.setType("user");
		mj.setIsParent(false);
		return mj;
	}
	
	
	public static DeptNode convertDept2Node(DepartmentAccountService departmentAccountService,long enterpriseId, Department dept) {
		DeptNode mj = new DeptNode();
		mj.setUserId(String.valueOf(dept.getDepartmentId()));
		mj.setpId(String.valueOf(dept.getParentId()));
		mj.setName(dept.getName());
		mj.setType("department");
		DepartmentAccount departmentAccount = departmentAccountService.getByDeptIdAndEnterpriseId(dept.getDepartmentId(),enterpriseId);
		if(departmentAccount!=null){
		    mj.setId(departmentAccount.getCloudUserId()+"");
			mj.setApprove(departmentAccount.getFileNeedApprove());
		}
		return mj;
	}

	public static String list2Json(List<DeptNode> list) {
		String result = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
			jsonGenerator.writeObject(list);
			result = objectMapper.writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

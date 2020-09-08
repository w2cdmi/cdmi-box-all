package com.huawei.sharedrive.uam.openapi.rest;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v2/department")
public class DepartmentAPIController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentAPIController.class);

	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	private UserTokenHelper userTokenHelper;

	@RequestMapping(value = "/getDeptInfo/{deptId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@RequestHeader("Authorization") String authorization, @RequestHeader(value = "Date", required = false) String date,@PathVariable("deptId") long deptId) {
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		Department department = departmentService.getByEnterpriseIdAndDepartmentId(userToken.getEnterpriseId(), deptId);
		return new ResponseEntity<>(department, HttpStatus.OK);
	}

	/*
	* 获取用户的所在的部门层级（返回部门的cloudUserId列表）。
	* */
	@RequestMapping(value = "/getDeptPathOfUser/{cloudUserId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMyDepartmentList(@RequestHeader("Authorization") String authorization, @PathVariable("cloudUserId") long cloudUserId) {
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);

		List<Long> deptList = new ArrayList<>();
		//UFM传递过来的参数是cloudUserId，不是userId。所以此处使用当前登录用户来查询
		if(userToken != null) {
			deptList = departmentService.getDeptCloudUserIdByUserId(userToken.getEnterpriseId(), userToken.getId(), userToken.getAccountId());
		} else {
			LOGGER.warn("Current session is null. authorization={}", authorization);
		}

		return new ResponseEntity<>(deptList, HttpStatus.OK);
	}
}
